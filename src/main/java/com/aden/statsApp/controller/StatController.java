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
        model.addAttribute("stats", statService.getStatWrapper().getCurrentStats());
        model.addAttribute("archive", statService.getStatWrapper().getArchivedStats());
        return "stats";
    }

    @PostMapping("/add")
    public String addStat(@RequestParam String statName) {
        statService.addNewStat(statName);
        return "redirect:/";
    }

    @PostMapping("/increment/{index}")
    public String incrementByOne(@PathVariable int index) {
        statService.incrementStatByOne(index);
        return "redirect:/";
    }

    @PostMapping("/addCustomAmount")
    public String addCustomAmount(@RequestParam int index, @RequestParam int count){
        statService.addCustomAmountToStatAtIndex(index, count);
        return "redirect:/";
    }

    @PostMapping("/archive")
    public String archiveStat(@RequestParam int index){
        statService.archiveStatAtIndex(index);
        return "redirect:/";
    }
}
