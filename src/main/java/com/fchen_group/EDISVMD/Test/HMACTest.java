package com.fchen_group.EDISVMD.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HexFormat;

public class HMACTest {
    public static byte[] calculateHmac(byte[] keyBytes, byte[] data, String algorithm) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm); // 直接使用字节数组
        Mac mac = Mac.getInstance(algorithm);
        mac.init(keySpec);
        return mac.doFinal(data);
    }

    public static byte[] readFile(String path) throws IOException {
        byte[] data = null;
        try {
            data = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\EDI-QZF\\experiment\\AppVendor\\64MB.txt";
        String hexKey = "3a7f2e4d1c0b8a5f6e9d8c7b6a5f4e3d";
        byte[] byteKey = HexFormat.of().parseHex(hexKey);
        byte[] data = readFile(filePath);
        long startTime = System.nanoTime();
        byte[] hmac = calculateHmac(byteKey, data, "HmacSHA256");
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(bytesToHex(hmac));
        System.out.println(totalTime);
    }
}
