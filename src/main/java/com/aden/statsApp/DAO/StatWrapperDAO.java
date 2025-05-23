package com.aden.statsApp.DAO;

import com.aden.statsApp.model.StatWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
public class StatWrapperDAO {

    @Value("${stat.file.path:stats.json}")
    private String statFileLocation;
    ObjectMapper objectMapper = new ObjectMapper();
    public void saveStatWrapperToFile(StatWrapper statWrapper) throws IOException {
        objectMapper.writeValue(new File(statFileLocation), statWrapper);
    }

    public StatWrapper readWrapperFromFile() {
        try {
            return objectMapper.readValue(new File(statFileLocation), StatWrapper.class);
        } catch (IOException e) {
            System.out.println("Failed to read from file, file may be missing or damaged? Creating new StatWrapper");
            return new StatWrapper();
        }
    }
}
