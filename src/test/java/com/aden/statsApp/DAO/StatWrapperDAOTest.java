package com.aden.statsApp.DAO;

import com.aden.statsApp.model.StatWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatWrapperDAOTest {

    private StatWrapperDAO dao;
    private ObjectMapper mockMapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        this.dao = new StatWrapperDAO();
        dao.setStatFileLocation(tempDir.resolve("stats.json").toString());
        dao.setMaxBackupFilesCount(2);
        this.mockMapper = Mockito.mock(ObjectMapper.class);
        dao.objectMapper = this.mockMapper;

        dao.setBackupDirectory(tempDir.resolve("aden/statsApp").toString());
        new File(dao.getBackupDirectory()).mkdirs();
    }

    @Test
    void testSaveStatWrapperInvokesWriteValueWithCorrectParameters() throws Exception {
        StatWrapper statWrapper = new StatWrapper();

        dao.saveStatWrapperToFile(statWrapper);

        verify(this.dao.objectMapper).writeValue(new File(dao.getStatFileLocation()), statWrapper);
    }

    @Test
    void testReadWrapperCorrectlyReadsWrapperAndInvokesCorrectMethods() throws Exception {
        StatWrapper testWrapper = new StatWrapper();
        when(mockMapper.readValue(any(File.class), eq(StatWrapper.class))).thenReturn(testWrapper);

        StatWrapper result = dao.readWrapperFromFile();

        assertSame(testWrapper, result);
        verify(this.dao.objectMapper).readValue(any(File.class), eq(StatWrapper.class));
    }

    @Test
    void testInCaseOfExceptionEmptyStatWrapperIsCreated() throws Exception {
        when(mockMapper.readValue(any(File.class), eq(StatWrapper.class)))
                .thenThrow(new IOException("test exeption"));

        StatWrapper result = dao.readWrapperFromFile();

        assertNotNull(result);
        assertTrue(result.getCurrentStats().isEmpty());
        assertTrue(result.getArchivedStats().isEmpty());
    }

    @Test
    void updateBackupsFolder_createsBackupFile() throws Exception {
        File statFile = new File(dao.getStatFileLocation());
        FileUtils.writeStringToFile(statFile, "{}", "UTF-8");
        dao.objectMapper = new ObjectMapper();

        dao.updateBackupsFolder();

        File[] backups = new File(dao.getBackupDirectory()).listFiles();
        assertNotNull(backups);
        assertTrue(backups.length > 0);
        String expectedFileDate = LocalDate.now().toString();
        assertEquals(String.format("%s.json", expectedFileDate), backups[0].getName());
    }

    @Test
    void testDeleteExtraFilesFromBackupDirectoryDeletesCorrectFiles() throws Exception {
        // Create dummy backup files, increasing their age
        for (int i = 0; i < 4; i++) {
            File f = new File(dao.getBackupDirectory(), "backup" + i + ".json");
            FileUtils.writeStringToFile(f, "{}", "UTF-8");
            f.setLastModified(System.currentTimeMillis() - (1000L * (i + 1)));
        }

        dao.deleteExtraFilesFromBackupDirectory();

        List<File> remaining = Arrays.asList(Objects.requireNonNull(new File(dao.getBackupDirectory()).listFiles()));
        assertNotNull(remaining);
        assertTrue(remaining.size() <= dao.getMaxBackupFilesCount());
        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), "backup0.json")));
        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), "backup1.json")));
        assertFalse(remaining.contains(new File(dao.getBackupDirectory(), "backup2.json")));
        assertFalse(remaining.contains(new File(dao.getBackupDirectory(), "backup3.json")));

    }

    @Test
    void testUpdateBackupsFolderDoesntCleanupFilesIfTodaysFileIsPresent() throws Exception {
        // Create dummy backup files, increasing their age
        for (int i = 0; i < 4; i++) {
            File f = new File(dao.getBackupDirectory(), "backup" + i + ".json");
            FileUtils.writeStringToFile(f, "{}", "UTF-8");
            f.setLastModified(System.currentTimeMillis() - (1000L * (i + 1)));
        }
        String todayDate = LocalDate.now().toString();
        String todayFileName = todayDate + ".json";
        File todayFile = new File(dao.getBackupDirectory(), todayFileName);
        FileUtils.writeStringToFile(todayFile, "{}", "UTF-8");


        dao.updateBackupsFolder();
        List<File> remaining = Arrays.asList(Objects.requireNonNull(new File(dao.getBackupDirectory()).listFiles()));
        for (File f : remaining) {
            System.out.println(f.getName());
        }
        assertNotNull(remaining);

        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), "backup0.json")));
        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), "backup1.json")));
        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), "backup2.json")));
        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), "backup3.json")));
        assertTrue(remaining.contains(new File(dao.getBackupDirectory(), todayFileName)));
    }

    @Test
    void testIoExceptionWhenCopyingFileToBackupThrowsRuntimeException() throws IOException {
        File f = new File(dao.getStatFileLocation());
        FileUtils.writeStringToFile(f, "{}", "UTF-8");
        try (MockedStatic<FileUtils> mocked = mockStatic(FileUtils.class)) {
            mocked.when(() -> FileUtils.copyFile(any(File.class), any(File.class))).thenThrow(new IOException("test IO Exception"));

            assertThrows(RuntimeException.class, () -> dao.updateBackupsFolder());
        }
    }
}