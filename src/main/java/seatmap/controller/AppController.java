package seatmap.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import seatmap.repository.ParticipantRepository;
import seatmap.repository.SeatRepository;
import seatmap.retriever.RandomNumberRetriever;

@Controller
public class AppController {
    private final RandomNumberRetriever randomGenerator;
    private final ParticipantRepository pRepo;
    private final SeatRepository sRepo;

    @Autowired
    public AppController(RandomNumberRetriever randomGenerator, ParticipantRepository pRepo, SeatRepository sRepo) {
        this.randomGenerator = randomGenerator;
        this.pRepo = pRepo;
        this.sRepo = sRepo;
    }

    @RequestMapping(value = "/")
    public ModelAndView index() {
        final Map<String, Object> model = new HashMap<>();
        model.put("randomNumber", 42);// randomGenerator.retrieveNewSeedNumber(Application.RANDOM_INT_URL));
        model.put("seats", sRepo.findAll());
        model.put("participants", pRepo.findAll());
        return new ModelAndView("index", model);
    }
}
