package it.unipd.webapp.service;

import it.unipd.webapp.helpers.AesFileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Utils {
    private static final String ENCRYPTED_DIRECTORY = "encrypted";

    public static String saveFile(MultipartFile file) throws Exception {
        if (file == null) {
            return null;
        }
        Path encryptedDirectory = Paths.get(ENCRYPTED_DIRECTORY);
        if (!Files.exists(encryptedDirectory)) {
            Files.createDirectories(encryptedDirectory);
        }
        byte[] fileBytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String encryptedFilename = UUID.randomUUID().toString() + extension;
        Path outputFile = Paths.get(ENCRYPTED_DIRECTORY, encryptedFilename);
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            AesFileUtils.encryptFile(inputStream, outputFile);
        }
        return encryptedFilename;
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

    public static boolean validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        long fileSize = file.getSize();

        if (!Arrays.asList("image/jpeg", "image/jpg", "image/png").contains(contentType)) {
            return false; // unsupported content type
        }

        if (!Arrays.asList("jpg", "jpeg", "png").contains(extension.toLowerCase())) {
            return false; // unsupported file extension
        }

        if (fileSize > 10000000) {
            return false; // file size exceeds the maximum allowed size of 10 MB
        }

        return true; // file is valid
    }
}
