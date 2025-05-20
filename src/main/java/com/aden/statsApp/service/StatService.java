package com.aden.statsApp.service;

import com.aden.statsApp.model.Stat;
import com.aden.statsApp.model.StatWrapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
public class StatService {
    private StatWrapper statWrapper;

    @Autowired
    public StatService(StatWrapper statWrapper){
        this.statWrapper = statWrapper;
    }

    public void addNewStat(String newStatName) {
        Stat newStat = new Stat(newStatName, 0);
        statWrapper.getCurrentStats().add(newStat);
    }

    public void incrementStatByOne(int statIndex) {
        incrementStatByValue(statIndex, 1);
    }

    public void addCustomAmountToStatAtIndex(int index, int count) {
        incrementStatByValue(index, count);
    }

    public void archiveStatAtIndex(int index) {
        if (index >= 0 && index < statWrapper.getCurrentStats().size()) {
            statWrapper.getArchivedStats().add(statWrapper.getCurrentStats().get(index));
            statWrapper.getCurrentStats().remove(index);
        }
    }

    private void incrementStatByValue(int index, int count) {
        if (index >= 0 && index < statWrapper.getCurrentStats().size()) {
            Stat stat = statWrapper.getCurrentStats().get(index);
            stat.setCount(stat.getCount() + count);
        }
    }
}
