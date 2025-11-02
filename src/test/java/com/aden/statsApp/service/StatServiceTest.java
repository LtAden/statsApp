package com.aden.statsApp.service;

import com.aden.statsApp.DAO.StatWrapperDAO;
import com.aden.statsApp.model.Stat;
import com.aden.statsApp.model.StatWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatServiceTest {
    private final Stat sampleStat = new Stat(0);
    private final String mockStatName = "Mock Stat";

    private StatService setupStatServiceWithOneStat() {
        StatWrapperDAO mockDao = Mockito.mock(StatWrapperDAO.class);

        StatWrapper mockWrapper = new StatWrapper();
        mockWrapper.getCurrentStats().put(mockStatName, sampleStat);
        when(mockDao.readWrapperFromFile()).thenReturn(mockWrapper);

        return new StatService(mockDao);
    }

    @Test
    void testCanAddNewStat() {
        StatWrapperDAO mockDao = Mockito.mock(StatWrapperDAO.class);

        StatWrapper mockWrapper = new StatWrapper();
        when(mockDao.readWrapperFromFile()).thenReturn(mockWrapper);

        StatService statService = new StatService(mockDao);
        assertEquals(statService.getStatWrapper().getCurrentStats().size(), 0);
        verify(mockDao, times(1)).readWrapperFromFile();

        statService.addNewStat("New Stat");
        assertAll(
                () -> assertEquals(1, statService.getStatWrapper().getCurrentStats().size()),
                () -> assertTrue(statService.getStatWrapper().getCurrentStats().containsKey("New Stat")),
                () -> assertEquals(0, statService.getStatWrapper().getCurrentStats().get("New Stat").getCount()),
                () -> assertEquals(0, statService.getStatWrapper().getArchivedStats().size())
        );
        verify(mockDao, times(2)).readWrapperFromFile();
    }

    @Test
    void testStatCanBeIncrementedByOneAndDaoIsRefreshedFromFileEveryTime() {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertAll(
                () -> assertTrue(statService.getStatWrapper().getCurrentStats().containsKey(mockStatName)),
                () -> assertEquals(statService.getStatWrapper().getCurrentStats().get(mockStatName).getCount(), 0)
        );

        statService.incrementStatByOne(mockStatName);
        verify(statService.getStatWrapperDAO(), times(2)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(mockStatName).getCount(), 1);
    }

    @Test
    void testAddCustomAmountPersistsValuesAndReadsFromFileEveryTime() {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(mockStatName).getCount(), 0);

        statService.addCustomAmountToStatByName(mockStatName, 37);
        verify(statService.getStatWrapperDAO(), times(2)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(mockStatName).getCount(), 37);

        statService.addCustomAmountToStatByName(mockStatName, 3);
        verify(statService.getStatWrapperDAO(), times(3)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(mockStatName).getCount(), 40);
    }

    @Test
    void testArchiveStatAndConfirmDaoWasReadFromFileAfterEachOperation() {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertAll(
                () -> assertEquals(1, statService.getStatWrapper().getCurrentStats().size()),
                () -> assertEquals(0, statService.getStatWrapper().getArchivedStats().size())
        );

        statService.archiveStatByName(mockStatName);
        verify(statService.getStatWrapperDAO(), times(2)).readWrapperFromFile();
        assertAll(
                () -> assertEquals(0, statService.getStatWrapper().getCurrentStats().size()),
                () -> assertEquals(1, statService.getStatWrapper().getArchivedStats().size()),
                () -> assertTrue(statService.getStatWrapper().getArchivedStats().containsKey(mockStatName)),
                () -> assertEquals(sampleStat, statService.getStatWrapper().getArchivedStats().get(mockStatName))
        );
    }

    @Test
    void testStatThrowsRuntimeExceptionWhenEncounteringIoExceptionWhenSavingAndUpdatingStatFile() throws IOException {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(mockStatName).getCount(), 0);

        doThrow(new IOException("test exception"))
                .when(statService.getStatWrapperDAO())
                .saveStatWrapperToFile(any(StatWrapper.class));

        assertThrows(RuntimeException.class, () -> statService.incrementStatByOne(mockStatName));

    }

    @Test
    void testGetSortedStatsReturnsStatsOrderedByName() {
        StatWrapperDAO mockDao = Mockito.mock(StatWrapperDAO.class);

        StatWrapper mockWrapper = new StatWrapper();
        when(mockDao.readWrapperFromFile()).thenReturn(mockWrapper);

        StatService statService = new StatService(mockDao);
        assertEquals(statService.getStatWrapper().getCurrentStats().size(), 0);
        verify(mockDao, times(1)).readWrapperFromFile();

        statService.addNewStat("New Stat");
        statService.addNewStat("An Even Newer Stat");
        statService.addNewStat("Old New Stat");

        Map<String, Stat> currentStats = statService.getSortedCurrentStats();

        assertAll(
                () -> assertEquals(3, statService.getStatWrapper().getCurrentStats().size()),
                () -> {
                    List<String> keys = new ArrayList<>(currentStats.keySet());
                    List<String> sortedKeys = new ArrayList<>(keys);
                    Collections.sort(sortedKeys);
                    assertEquals(sortedKeys, keys, "Stats are not ordered alphabetically by name");
                }
        );
    }

    @Test
    void testGetSortedArchiveStatsReturnsStatsOrderedByName() {
        StatWrapperDAO mockDao = Mockito.mock(StatWrapperDAO.class);

        StatWrapper mockWrapper = new StatWrapper();
        when(mockDao.readWrapperFromFile()).thenReturn(mockWrapper);

        StatService statService = new StatService(mockDao);
        assertEquals(statService.getStatWrapper().getCurrentStats().size(), 0);
        verify(mockDao, times(1)).readWrapperFromFile();

        statService.addNewStat("New Stat");
        statService.archiveStatByName("New Stat");
        statService.addNewStat("An Even Newer Stat");
        statService.archiveStatByName("An Even Newer Stat");
        statService.addNewStat("Old New Stat");
        statService.archiveStatByName("Old New Stat");

        Map<String, Stat> sortedArchivedStats = statService.getSortedArchivedStats();

        assertAll(
                () -> assertEquals(3, statService.getStatWrapper().getArchivedStats().size()),
                () -> {
                    List<String> keys = new ArrayList<>(sortedArchivedStats.keySet());
                    List<String> sortedKeys = new ArrayList<>(keys);
                    Collections.sort(sortedKeys);
                    assertEquals(sortedKeys, keys, "Stats are not ordered alphabetically by name");
                }
        );
    }
}