package com.aden.statsApp.controller;

import com.aden.statsApp.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class StatController {
    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping
    public String viewStats(Model model) {
        model.addAttribute("stats", statService.getSortedCurrentStats());
        model.addAttribute("archive", statService.getSortedArchivedStats());
        return "stats";
    }

    @PostMapping("/add")
    public String addStat(@RequestParam String statName) {
        statService.addNewStat(statName);
        return "redirect:/";
    }

    @PostMapping("/increment")
    public String incrementByOne(@RequestParam String statName) {
        statService.incrementStatByOne(statName);
        return "redirect:/";
    }

    @PostMapping("/addCustomAmount")
    public String addCustomAmount(@RequestParam String statName, @RequestParam int count) {
        statService.addCustomAmountToStatByName(statName, count);
        return "redirect:/";
    }

    @PostMapping("/archive")
    public String archiveStat(@RequestParam String statName) {
        statService.archiveStatByName(statName);
        return "redirect:/";
    }
}
