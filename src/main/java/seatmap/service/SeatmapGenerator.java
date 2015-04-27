package seatmap.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import seatmap.Application;
import seatmap.NotEnoughRoomsException;
import seatmap.repository.ParticipantRepository;
import seatmap.repository.RoomRepository;
import seatmap.retriever.RandomNumberRetriever;
import be.quodlibet.boxable.PdfCell;
import be.quodlibet.boxable.PdfRow;
import be.quodlibet.boxable.PdfTable;

/**
 * @author: Paul Bochis, Catalysts GmbH
 */
@Service
public class SeatmapGenerator {

    private final RandomNumberRetriever randomGenerator;
    private final ParticipantRepository pRepo;
    private final RoomRepository roomRepository;

    @Autowired
    public SeatmapGenerator(RandomNumberRetriever randomGenerator, ParticipantRepository pRepo, RoomRepository roomRepository) {
        this.randomGenerator = randomGenerator;
        this.pRepo = pRepo;
        this.roomRepository = roomRepository;
    }

    private static final int NUMBER_OF_TALBES_IN_ROOM = 8;

    public List<String> readData(String filename, boolean sorted) {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(filename));
            final List<String> data = new ArrayList<String>();
            String entity;
            while ((entity = br.readLine()) != null) {
                if (!entity.equals("")) {
                    data.add(entity);
                }
            }
            br.close();
            if (sorted) {
                Collections.sort(data);
            } else {
                Collections.shuffle(data);
            }
            return data;
        } catch (final Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public void writeData(String filename, List<String> values) {
        try {
            Collections.sort(values);
            final BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (final String s : values) {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
        } catch (final Exception io) {
            System.out.println(io);
        }
    }

    public void generateSeatmap(Date date, OutputStream outputStream) throws NotEnoughRoomsException {
        final List<String> employees = pRepo.findNames();
        final List<String> rooms = roomRepository.findNames();
        Collections.shuffle(rooms, new Random(randomGenerator.retrieveNewSeedNumber(Application.RANDOM_INT_URL)));
        Collections.shuffle(employees, new Random(randomGenerator.retrieveNewSeedNumber(Application.RANDOM_INT_URL)));
        final LinkedHashMap<String, List<String>> roomsWithTables = expandRooms(rooms);
        generateSeatMap(roomsWithTables, rooms, employees, date, outputStream);
    }

    private void generateSeatMap(LinkedHashMap<String, List<String>> roomsWithTables, List<String> rooms,
            List<String> employees, Date date, OutputStream outputStream) throws NotEnoughRoomsException {
        final int totalEmployees = employees.size();
        final LinkedHashMap<String, String> assignedSeats = new LinkedHashMap<String, String>();
        final Random rand = new Random();
        int i = 0;
        int randomSpot;
        int randomEmployee;
        while (employees.size() > 0) {
            final String room = rooms.get(i);
            if (roomsWithTables.get(room).size() <= 0) {
                throw new NotEnoughRoomsException(totalEmployees);
            }
            randomSpot = rand.nextInt(roomsWithTables.get(room).size());
            randomEmployee = rand.nextInt(employees.size());
            assignedSeats.put(employees.get(randomEmployee), roomsWithTables.get(room).get(randomSpot));
            employees.remove(randomEmployee);
            roomsWithTables.get(room).remove(randomSpot);
            i = i == rooms.size() - 1 ? 0 : i + 1;
        }
        generateSeatmapDocument(assignedSeats, date, outputStream);
    }

    private LinkedHashMap<String, String> invertMap(LinkedHashMap<String, String> map) {
        final LinkedHashMap<String, String> newMap = new LinkedHashMap<String, String>();
        for (final String key : map.keySet()) {
            newMap.put(map.get(key), key);
        }
        return newMap;
    }

    private void generateSeatmapDocument(LinkedHashMap<String, String> seats, Date date, OutputStream outputStream) {
        final float Margin = 10;
        seats = invertMap(seats);
        try {
            final PDDocument doc = new PDDocument();
            final PDPage page = addNewPage(doc);
            final PDPageContentStream pageContentStream = new PDPageContentStream(doc, page);
            final float tableWidth = page.findMediaBox().getWidth() - (2 * Margin);
            final float top = page.findMediaBox().getHeight() - (2 * Margin);
            final PdfTable table = new PdfTable((top - (1 * 20f)), Margin, page, pageContentStream);

            final PdfRow headerrow = new PdfRow(25f);
            final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            final Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 14);
            final Date toDate = cal.getTime();

            final PdfCell cell = new PdfCell(tableWidth, "Assigned seats starting from " + df.format(date) + " to "
                    + df.format(toDate));
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(16);
            headerrow.addCell(cell);
            table.drawRow(headerrow);

            final List<String> tables = new ArrayList<String>(seats.keySet());
            Collections.sort(tables);
            for (final String seat : tables) {
                final String employee = seats.get(seat);
                final PdfRow row = new PdfRow(20f);
                row.addCell(new PdfCell(tableWidth / 2, employee));
                row.addCell(new PdfCell(tableWidth / 2, seat));
                table.drawRow(row);
            }
            table.endTable(tableWidth);
            pageContentStream.close();
            doc.save(outputStream);
        } catch (final Exception io) {
            System.out.println(io);
        }

    }

    private PDPage addNewPage(PDDocument doc) {
        final PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }

    private LinkedHashMap<String, List<String>> expandRooms(List<String> rooms) {
        final LinkedHashMap<String, List<String>> roomsWithTables = new LinkedHashMap<String, List<String>>();
        for (final String room : rooms) {
            final List<String> tables = new ArrayList<String>();
            for (int i = 1; i <= NUMBER_OF_TALBES_IN_ROOM; i++) {
                tables.add(room + "." + i);
            }
            roomsWithTables.put(room, tables);
        }
        return roomsWithTables;
    }

}
