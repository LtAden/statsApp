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

    @GetMapping
    public String viewStats(Model model) {
        model.addAttribute("stats", stats);
        return "stats";
    }

    @GetMapping("/")
    public String redirToStats(){
        return "redirect:/stats";
    }

    @PostMapping("/add")
    public String addStat(@RequestParam String statName) {
        stats.add(new Stat(statName, 0)); // Add stat with a count of 0
        return "redirect:/stats";
    }

    @PostMapping("/increment/{index}")
    public String incrementByOne(@PathVariable int index) {
        if (index >= 0 && index < stats.size()) {
            Stat stat = stats.get(index);
            stat.setCount(stat.getCount() + 1);
        }
        return "redirect:/stats";
    }

    @PostMapping("/addCustomAmount")
    public String addCustomAmount(@RequestParam int index, @RequestParam int count){
        if (index >= 0 && index < stats.size()) {
            Stat stat = stats.get(index);
            stat.setCount(stat.getCount() + count);
        }
        return "redirect:/stats";
    }
}
