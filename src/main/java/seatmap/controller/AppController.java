package seatmap.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import seatmap.Application;
import seatmap.retriever.RandomNumberRetriever;

@Controller
public class AppController {
    private final RandomNumberRetriever randomGenerator;

    @Autowired
    public AppController(RandomNumberRetriever randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @RequestMapping(value = "/")
    public ModelAndView index() {
        final Map<String, Object> model = new HashMap<>();
        model.put("randomNumber", randomGenerator.retrieveNewSeedNumber(Application.RANDOM_INT_URL));
        return new ModelAndView("index", model);
    }
}
