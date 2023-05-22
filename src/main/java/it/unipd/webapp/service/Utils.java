package it.unipd.webapp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

public class Utils {
    public static String saveFile(MultipartFile file) throws IOException {
        // Save file to disk with randomized filename
        String filename = UUID.randomUUID().toString() + "." + getFileExtension(file.getOriginalFilename());
        File directory = new File("uploads");
        if (!directory.exists()) {
            directory.mkdir();
        }
        Path path = Paths.get(directory.getAbsolutePath() + File.separator + filename);
        try {
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to save file!");
        }
        return filename;
    }

    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static String encodeFileToBase64(String filePath) throws IOException {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fis.read(buffer);
        fis.close();
        return Base64.getEncoder().encodeToString(buffer);
    }
}
