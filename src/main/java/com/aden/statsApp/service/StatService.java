package com.aden.statsApp.service;

import com.aden.statsApp.DAO.StatWrapperDAO;
import com.aden.statsApp.model.Stat;
import com.aden.statsApp.model.StatWrapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

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
        Stat newStat = new Stat(newStatName, 0);
        statWrapper.getCurrentStats().add(newStat);
        sortListByStatName(statWrapper.getCurrentStats());
        saveAndReloadStats();
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
            sortListByStatName(statWrapper.getArchivedStats());
            saveAndReloadStats();
        }
    }

    private void incrementStatByValue(int index, int count) {
        if (index >= 0 && index < statWrapper.getCurrentStats().size()) {
            Stat stat = statWrapper.getCurrentStats().get(index);
            stat.setCount(stat.getCount() + count);
            saveAndReloadStats();
        }
    }

    private void saveAndReloadStats() {
        try {
            statWrapperDAO.saveStatWrapperToFile(this.statWrapper);
            this.statWrapper = statWrapperDAO.readWrapperFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sortListByStatName(List<Stat> listToSort) {
        listToSort.sort(Comparator.comparing(Stat::getStatName));
    }
}
