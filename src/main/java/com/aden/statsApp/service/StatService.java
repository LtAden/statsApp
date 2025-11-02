package com.aden.statsApp.service;

import com.aden.statsApp.DAO.StatWrapperDAO;
import com.aden.statsApp.model.Stat;
import com.aden.statsApp.model.StatWrapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Getter
public class StatService {
    private StatWrapper statWrapper;
    private final StatWrapperDAO statWrapperDAO;

    @Autowired
    public StatService(StatWrapperDAO statWrapperDAO) {
        this.statWrapperDAO = statWrapperDAO;
        this.statWrapper = statWrapperDAO.readWrapperFromFile();
    }

    public void addNewStat(String newStatName) {
        Stat newStat = new Stat(0);
        statWrapper.getCurrentStats().put(newStatName, newStat);
        saveAndReloadStats();
    }

    public void incrementStatByOne(String statName) {
        incrementStatByValue(statName, 1);
    }

    public void addCustomAmountToStatByName(String statName, int count) {
        incrementStatByValue(statName, count);
    }

    public void archiveStatByName(String statName) {
        statWrapper.getArchivedStats().put(statName, statWrapper.getCurrentStats().get(statName));
        statWrapper.getCurrentStats().remove(statName);
        saveAndReloadStats();
    }

    private void incrementStatByValue(String name, int count) {
        Stat stat = statWrapper.getCurrentStats().get(name);
        stat.setCount(stat.getCount() + count);
        saveAndReloadStats();
    }

    private void saveAndReloadStats() {
        try {
            statWrapperDAO.saveStatWrapperToFile(this.statWrapper);
            this.statWrapper = statWrapperDAO.readWrapperFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
