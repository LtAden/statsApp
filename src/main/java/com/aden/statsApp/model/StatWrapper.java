package com.aden.statsApp.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class StatWrapper {
    private List<Stat> currentStats;
    private List<Stat> archivedStats;

    public StatWrapper(){
        currentStats = new ArrayList<>();
        archivedStats = new ArrayList<>();
    }
}
