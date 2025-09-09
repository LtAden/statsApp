package com.aden.statsApp.service;

import com.aden.statsApp.DAO.StatWrapperDAO;
import com.aden.statsApp.model.Stat;
import com.aden.statsApp.model.StatWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatServiceTest {
    private final Stat sampleStat = new Stat("New Stat", 0);

    private StatService setupStatServiceWithOneStat() {
        StatWrapperDAO mockDao = Mockito.mock(StatWrapperDAO.class);
        StatWrapper mockWrapper = new StatWrapper();
        mockWrapper.getCurrentStats().add(sampleStat);
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
                () -> assertEquals(0, statService.getStatWrapper().getArchivedStats().size()),
                () -> assertEquals("New Stat", statService.getStatWrapper().getCurrentStats().get(0).getStatName()),
                () -> assertEquals(0, statService.getStatWrapper().getCurrentStats().get(0).getCount())
        );
        verify(mockDao, times(2)).readWrapperFromFile();
    }

    @Test
    void testStatCanBeIncrementedByOneAndDaoIsRefreshedFromFileEveryTime() {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(0).getCount(), 0);

        statService.incrementStatByOne(0);
        verify(statService.getStatWrapperDAO(), times(2)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(0).getCount(), 1);
    }

    @Test
    void testAddCustomAmountPersistsValuesAndReadsFromFileEveryTime() {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(0).getCount(), 0);

        statService.addCustomAmountToStatAtIndex(0, 37);
        verify(statService.getStatWrapperDAO(), times(2)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(0).getCount(), 37);

        statService.addCustomAmountToStatAtIndex(0, 3);
        verify(statService.getStatWrapperDAO(), times(3)).readWrapperFromFile();
        assertEquals(statService.getStatWrapper().getCurrentStats().get(0).getCount(), 40);
    }

    @Test
    void testArchiveStatAndConfirmDaoWasReadFromFileAfterEachOperation() {
        StatService statService = setupStatServiceWithOneStat();
        verify(statService.getStatWrapperDAO(), times(1)).readWrapperFromFile();
        assertAll(
                () -> assertEquals(1, statService.getStatWrapper().getCurrentStats().size()),
                () -> assertEquals(0, statService.getStatWrapper().getArchivedStats().size())
        );

        statService.archiveStatAtIndex(0);
        verify(statService.getStatWrapperDAO(), times(2)).readWrapperFromFile();
        assertAll(
                () -> assertEquals(0, statService.getStatWrapper().getCurrentStats().size()),
                () -> assertEquals(1, statService.getStatWrapper().getArchivedStats().size()),
                () -> assertEquals(sampleStat, statService.getStatWrapper().getArchivedStats().get(0))
        );
    }
}