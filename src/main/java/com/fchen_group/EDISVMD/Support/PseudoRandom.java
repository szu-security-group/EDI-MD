package com.fchen_group.EDISVMD.Support;

import java.util.*;
import javax.crypto.Cipher;

import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;


public class PseudoRandom {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/NoPadding";

    private final SecretKeySpec secretKey;
    private Cipher cipher;

    public PseudoRandom(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("key must be 16 bytes");
        }
        this.secretKey = new SecretKeySpec(key, ALGORITHM);
        try {
            this.cipher = Cipher.getInstance(TRANSFORMATION);
            this.cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (Exception e){
            throw new RuntimeException("AES initialization failed",e);
        }
    }

    public byte[] generate (int seed) {
        byte[] inputBytes = new byte[16];
        ByteBuffer.wrap(inputBytes).putInt(seed);

        try{
            return cipher.doFinal(inputBytes);
        } catch (Exception e){
            throw new RuntimeException("AES initialization failed",e);
        }
    }

//    public static void main(String[] args) {
//        // 示例密钥（实际应用中应使用安全随机生成的密钥）
//        byte[] key = new byte[16];
//        Arrays.fill(key, (byte) 0xAA); // 示例模式密钥
//
//        PseudoRandom pseudoRandom = new PseudoRandom(key);
//
//        int seed1 = 1;
//        int seed2 = 12345;
//
//        // 生成伪随机数
//        byte[] random1 = pseudoRandom.generate(seed1);
//        System.out.println(random1.length);
//        byte[] random2 = pseudoRandom.generate(seed2);
//        System.out.println(random2.length);
//
//        System.out.println("Seed " + seed1 + ": " + bytesToHex(random1));
//        System.out.println("Seed " + seed2 + ": " + bytesToHex(random2));
//    }
//
//    private static String bytesToHex(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }


}




