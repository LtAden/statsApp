package com.aden.statsApp.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class StatWrapper {
    private List<Stat> currentStats;
    private List<Stat> archivedStats;

    public StatWrapper(){
        currentStats = new ArrayList<>();
        archivedStats = new ArrayList<>();
    }
}
