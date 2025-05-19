package com.aden.statsApp.controller;

import com.aden.statsApp.model.Stat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/stats")
public class StatController {
    private final List<Stat> stats = new ArrayList<>();

    // Display all stats
    @GetMapping
    public String viewStats(Model model) {
        model.addAttribute("stats", stats);
        return "stats"; // Thymeleaf template name: stats.html
    }

    // Add a new stat
    @PostMapping("/add")
    public String addStat(@RequestParam String statName) {
        stats.add(new Stat(statName, 0)); // Add stat with a count of 0
        return "redirect:/stats";
    }

    // Increment the count of a stat
    @PostMapping("/increment/{index}")
    public String incrementStat(@PathVariable int index) {
        if (index >= 0 && index < stats.size()) {
            Stat stat = stats.get(index);
            stat.setCount(stat.getCount() + 1);
        }
        return "redirect:/stats";
    }
}
