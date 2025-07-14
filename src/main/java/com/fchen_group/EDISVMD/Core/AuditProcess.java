package com.fchen_group.EDISVMD.Core;

import com.fchen_group.EDISVMD.Support.Galois;
import com.fchen_group.EDISVMD.Support.PseudoRandom;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.security.MessageDigest;  // 核心类
import java.security.NoSuchAlgorithmException;

public class AuditProcess extends AuditComponent {
    public int BLOCK_NUM;

    // The table of situation of data storage. If dataSituation[i][j] == true, the replica of i th original data is cached on j th edge server.
    public boolean[][] dataSituation;

    public String originalPath;
    public String replicaPath;

    public AuditProcess(int BLOCK_NUM, boolean[][] dataSituation, String originalPath, String replicaPath) {
        this.BLOCK_NUM = BLOCK_NUM;
        this.dataSituation = dataSituation;
        this.originalPath = originalPath;
        this.replicaPath = replicaPath;
    }

    public Key keyGen(int len) {
        System.out.println("KeyGen phase start.");
        String chars1 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String chars2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuffer strBuff1 = new StringBuffer();
        StringBuffer strBuff2 = new StringBuffer();
        for (int i = 0; i < len; i++) {
            strBuff1.append(chars1.charAt(new Random().nextInt(chars1.length())));
            strBuff2.append(chars2.charAt(new Random().nextInt(chars2.length())));
        }
        Key key = new Key(strBuff1.toString(), strBuff2.toString());

        System.out.println("KeyGen phase end.");

        return key;
    }

    public void outSource(){
        System.out.println("Outsource phase start.");

        System.out.println("Outsource phase end.");
    }


    public ChallengeData[] audit(int esNum, Key key) {
        System.out.println("Audit phase start.");

        ChallengeData[] challengeDatas = new ChallengeData[esNum];

        for(int j = 0; j < esNum; j++){
            PseudoRandom pseudoRandom = new PseudoRandom(key.getKeyMaster());
            byte[] tempKey = pseudoRandom.generate(j);
            challengeDatas[j] = new ChallengeData(key.getKeyHMAC(), tempKey);
        }

        /**
         * Now the keyHMAC and keys[j] will be sent to the j th edge server.
         * Then, App Vendor calculate the correction coefficient for every original data end ramdomly sends to the
         * edge server holding the replica of related original data.
         */

        int m = this.dataSituation.length;
        for(int i = 0; i < m; i++){
            byte[] coefficientSum = new byte[this.BLOCK_NUM];
            for(int j = 0; j < esNum; j++){
                if(this.dataSituation[i][j]){
                    PseudoRandom pseudoRandom = new PseudoRandom(challengeDatas[j].keyMaster);
                    byte[] coefficient = pseudoRandom.generate(i);
                    for(int k = 0; k < this.BLOCK_NUM; k++){
                        coefficientSum[k] = Galois.add(coefficientSum[k], coefficient[k]);
                    }
                }
            }
            int randomIndex = new Random().nextInt(esNum);
            while(!dataSituation[i][randomIndex]){
                randomIndex = new Random().nextInt(esNum);
            }
            challengeDatas[randomIndex].correctionCoefficients.put(i,coefficientSum);
        }


        System.out.println("Audit phase end.");
        return challengeDatas;
    }

    /**
     *Here, the param [data] is the replica of i th original data in edge server.
     * For convenience, all the data is the same.
     * If you want different data, you can change the param to String[] paths, and read the file here.
     */

    public ProofData prove(int esIndex, byte[] data, ChallengeData challengeData) throws IOException {
        System.out.println("Prove phase start.");

        PseudoRandom pseudoRandom = new PseudoRandom(challengeData.keyMaster);

        byte[] tempRes = new byte[this.BLOCK_NUM];
        Map<Integer, byte[]> hmacs = new HashMap<>();
        for(int i = 0; i < this.dataSituation.length; i++){
            if(dataSituation[i][esIndex]){
                byte[] coefficient = pseudoRandom.generate(i);
                byte[] temp = new byte[this.BLOCK_NUM];
                byte[] hmac;
                try {
                    hmac = Arrays.copyOfRange(calculateHmac(challengeData.keyHMAC, data, "HmacSHA256"), 0, BLOCK_NUM);
                    hmacs.put(i, hmac);
                } catch(Exception e){
                    throw new IOException(e.getMessage());
                }
                for(int j = 0; j < this.BLOCK_NUM; j++){
                    temp[j] = Galois.multiply(coefficient[j], hmac[j]);
                    tempRes[j] = Galois.add(tempRes[j], temp[j]);
                }
            }
        }

        if(!challengeData.correctionCoefficients.isEmpty()){
            for(int key: challengeData.correctionCoefficients.keySet()){
                byte[] hmac = hmacs.get(key);
                byte[] temp = new byte[this.BLOCK_NUM];
                byte[] correctionCoefficient = challengeData.correctionCoefficients.get(key);
                for(int j = 0; j < this.BLOCK_NUM; j++){
                    temp[j] = Galois.multiply(correctionCoefficient[j], hmac[j]);
                    tempRes[j] = Galois.add(tempRes[j], temp[j]);
                }
            }
        }

        System.out.println("Prove phase end.");
        return new ProofData(tempRes);
    }

    public boolean verify(ProofData[] proofData) {
        System.out.println("Verify phase start.");

        boolean result = true;

        byte[] res = new byte[this.BLOCK_NUM];
        for(int i = 0; i < proofData.length; i++){
            for(int j = 0; j < BLOCK_NUM;j++){
                res[j] = Galois.add(res[j],proofData[i].combineProof[j]);
            }
        }

        for(int j = 0; j < BLOCK_NUM; j++){
            if(res[j] != (byte)0){
                result = false;
            }
        }

        System.out.println("Verify phase end.");
        return result;
    }

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
}


