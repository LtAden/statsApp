package com.aden.statsApp.DAO;

import com.aden.statsApp.model.StatWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@Getter
@Setter
public class StatWrapperDAO {

    @Value("${stat.file.path:stats.json}")
    private String statFileLocation;
    @Value("${stat.maxFileCount:3}")
    private int maxBackupFilesCount;
    private final String APP_DATA_KEY = "APPDATA";
    private final String APP_DATA_DIRECTORY = System.getenv(APP_DATA_KEY);
    private final String BACKUP_DIRECTORY_SUFFIX = "aden\\statsApp\\";
    private String backupDirectory = String.format("%s\\%s", APP_DATA_DIRECTORY, BACKUP_DIRECTORY_SUFFIX);
    ObjectMapper objectMapper = new ObjectMapper();

    @EventListener(ApplicationReadyEvent.class)
    public void updateBackupsFolder() {
        String todayBackupFileName = String.format("%s.json", getTodayDate());
        Set<String> backupDirectoryContents = listBackupDirectoryContents();
        if(!backupDirectoryContents.contains(todayBackupFileName)){
            createDailyBackupAndRemoveExtraFiles(todayBackupFileName);
            deleteExtraFilesFromBackupDirectory();
        }
    }

    public void saveStatWrapperToFile(StatWrapper statWrapper) throws IOException {
        objectMapper.writeValue(new File(statFileLocation), statWrapper);
    }

    public StatWrapper readWrapperFromFile() {
        File statFile = new File(statFileLocation);
        try {
            return objectMapper.readValue(statFile, StatWrapper.class);
        } catch (IOException e) {
            System.out.printf("Failed to read from file at expected directory of [%s], file may be missing or damaged? Creating new StatWrapper%n", statFile.getAbsolutePath());
            return new StatWrapper();
        }
    }

    private String getTodayDate(){
        LocalDate date = LocalDate.now();
        return date.toString();
    }

    private Set<String> listBackupDirectoryContents() {
        File[] listOfBackupFiles = new File(backupDirectory).listFiles();
        if(listOfBackupFiles == null || listOfBackupFiles.length == 0){
            return new HashSet<String>();
        }
        return Stream.of(listOfBackupFiles)
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    void createDailyBackupAndRemoveExtraFiles(String dateToday){
        File statFile = new File(statFileLocation);
        if(statFile.exists() && !statFile.isDirectory()){
            copyStatFileToBackupDirectory(dateToday);
        } else {
            System.out.println("Stat file does not exist yet. No backups created");
        }
    }

    private void copyStatFileToBackupDirectory(String dateToday){
        File targetFile = new File(String.format("%s\\%s", backupDirectory, dateToday));
        try {
            FileUtils.copyFile(new File(statFileLocation), targetFile);
            targetFile.setLastModified(System.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteExtraFilesFromBackupDirectory() {
        File[] logFiles = new File(backupDirectory).listFiles();
        while (logFiles != null && logFiles.length > maxBackupFilesCount) {
            long oldestDate = Long.MAX_VALUE;
            File oldestFile = null;
            for (File f : logFiles) {
                if (f.lastModified() < oldestDate) {
                    oldestDate = f.lastModified();
                    oldestFile = f;
                }
            }
            if (oldestFile != null) {
                oldestFile.delete();
            }

            logFiles = new File(backupDirectory).listFiles();
        }
    }
}
