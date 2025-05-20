package com.aden.statsApp.service;

import com.aden.statsApp.model.Stat;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Getter
public class StatService {
    private final List<Stat> stats = new ArrayList<>();
    private final List<Stat> archive = new ArrayList<>();

    public void addNewStat(String newStatName) {
        Stat newStat = new Stat(newStatName, 0);
        stats.add(newStat);
    }

    public void incrementStatByOne(int statIndex) {
        incrementStatByValue(statIndex, 1);
    }

    public void addCustomAmountToStatAtIndex(int index, int count) {
        incrementStatByValue(index, count);
    }

    public void archiveStatAtIndex(int index) {
        if (index >= 0 && index < stats.size()) {
            archive.add(stats.get(index));
            stats.remove(index);
        }
    }

    private void incrementStatByValue(int index, int count) {
        if (index >= 0 && index < stats.size()) {
            Stat stat = stats.get(index);
            stat.setCount(stat.getCount() + count);
        }
    }
}
