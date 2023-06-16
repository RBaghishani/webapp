package it.unipd.webapp.helpers;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;

public class AesFileUtils {

    private static final String secretKey = "POjRzp3OyiZUYmkYjtih6ozS32VzQwln";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    public static void encryptFile(InputStream inputStream, Path outputFile) throws Exception {
        byte[] key = secretKey.getBytes();
        byte[] iv = generateIv();

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        try (OutputStream outputStream = new CipherOutputStream(new FileOutputStream(outputFile.toFile()), cipher)) {
            outputStream.write(iv);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        Arrays.fill(key, (byte) 0);
        Arrays.fill(iv, (byte) 0);
    }

    public static void decryptFile(Path inputFile, OutputStream outputStream) throws Exception {
        byte[] key = secretKey.getBytes();

        try (InputStream inputStream = new FileInputStream(inputFile.toFile())) {
            byte[] iv = new byte[IV_SIZE];
            inputStream.read(iv);

            SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }

        Arrays.fill(key, (byte) 0);
    }

    private static byte[] generateIv() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv);
        return iv;
    }

}