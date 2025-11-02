package com.aden.statsApp.model;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class StatWrapper {
    private Map<String, Stat> currentStats;
    private Map<String, Stat> archivedStats;

    public StatWrapper(){
        currentStats = new HashMap<>();
        archivedStats = new HashMap<>();
    }
}
