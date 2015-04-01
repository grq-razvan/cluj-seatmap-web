package seatmap.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import seatmap.NotEnoughRoomsException;
import seatmap.entity.Participant;
import seatmap.repository.ParticipantRepository;
import seatmap.repository.SeatRepository;
import seatmap.retriever.RandomNumberRetriever;
import seatmap.service.SeatmapGenerator;

@Controller
public class AppController {
    private final SeatmapGenerator generator;
    private final RandomNumberRetriever randomGenerator;
    private final ParticipantRepository pRepo;
    private final SeatRepository sRepo;

    @Autowired
    public AppController(SeatmapGenerator generator, RandomNumberRetriever randomGenerator,
            ParticipantRepository pRepo, SeatRepository sRepo) {
        this.randomGenerator = randomGenerator;
        this.generator = generator;
        this.pRepo = pRepo;
        this.sRepo = sRepo;
    }

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("randomNumber", 42);// randomGenerator.retrieveNewSeedNumber(Application.RANDOM_INT_URL));
        model.addAttribute("seats", sRepo.findAll());
        model.addAttribute("participants", pRepo.findAll());
        return "index";
    }

    @RequestMapping(value = "/saveUser", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestParam("name") String name) {
        final Participant p = new Participant();
        p.setName(name);
        pRepo.save(p);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/seatmap", method = RequestMethod.GET)
    public void createSeatmap(HttpServletResponse response) throws NotEnoughRoomsException, IOException {
        generator.generateSeatmap(new Date(), response.getOutputStream());
        response.setContentType("application/pdf");
        response.flushBuffer();
    }
}
