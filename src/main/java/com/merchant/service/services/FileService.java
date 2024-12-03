package com.merchant.service.services;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void writeToFile(String filename, String content) throws IOException {
        try {
            // Get current date
            ZonedDateTime nowIst = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            String currentDate = nowIst.format(DATE_FORMATTER);
            String directoryPath =  "opt/tomcat/java-logs/register/"+filename;
            File directory = new File(directoryPath);
            // Create directories if they don't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }


            String fileName = directoryPath+ "/" + filename + "-" + currentDate + ".txt";

            File file = new File(fileName);

            // Create file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
            }

            // Write content to file

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(content);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            System.out.println("File Create Exception is : " + e.getMessage());
        }
    }
}
