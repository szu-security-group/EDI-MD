package com.fchen_group.EDISVMD.Run;

import com.fchen_group.EDISVMD.Core.AuditProcess;
import com.fchen_group.EDISVMD.Core.ChallengeData;
import com.fchen_group.EDISVMD.Core.Key;
import com.fchen_group.EDISVMD.Core.ProofData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class Benchmark {

    public static String originalPath;
    public static String replicaPath;

    public static int dataNum;
    public static int esNum;

    public static int BLOCK_NUM = 16;

    public static int EXP_TIME;

    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter the number of data:");
        dataNum = in.nextInt();
        System.out.println("Please enter the number of edge servers:");
        esNum = in.nextInt();
        System.out.println("Please enter the number of experiments:");
        EXP_TIME = in.nextInt();

        System.out.println("Please enter the filePath of  the original data ");
        in.nextLine();//读string 方法会把换行符当作字符串读进来，需要过滤掉
        originalPath = in.nextLine();
        System.out.println("Please enter the filePath of  the replica data ");

        replicaPath = in.nextLine();
        in.close();

//        dataNum = 10;
//        esNum = 512;
//        EXP_TIME = 1;
//
//        originalPath = "D:\\EDI-QZF\\experiment\\AppVendor\\512MB.txt";
//        replicaPath = "D:\\EDI-QZF\\experiment\\EdgeServer\\512MB.txt";

        new Benchmark().run();
    }

    public void run() throws Exception {
        long[][] sTime = new long[EXP_TIME][5];
        long[][] time = new long[EXP_TIME][5];
        SimpleDateFormat dfStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startProgramTime = dfStart.format(new Date());

        for(int t = 0; t < EXP_TIME; t++) {
            process(t, sTime, time);
        }
        String temp_filename = originalPath.lastIndexOf('\\') == -1 ? originalPath : originalPath.substring(originalPath.lastIndexOf('\\') + 1);
        String fileNameWithoutExtension = temp_filename.contains(".")
                ? temp_filename.substring(0, temp_filename.lastIndexOf('.'))
                : temp_filename;
        String newPath = "D:\\EDI-QZF\\experiment\\result\\" + fileNameWithoutExtension + "-dataNum_" + dataNum + "-esNum_" + esNum + "-EXP_TIME_" + EXP_TIME + ".txt";
        System.out.println(newPath);

        FileWriter resWriter = new FileWriter(newPath);
        File sourceFile = new File(originalPath);
        String fileName = sourceFile.getName();
        String titleLine = "PARAM:      dataNum  " + dataNum  + "        " + "esNum " + esNum + "     fileName     " + fileName + "\r\n";
        resWriter.write(titleLine);

        for (int i = 0; i < EXP_TIME; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < 5; j++) {
                sb.append(time[i][j]);
                if (j < 4) sb.append("                  ");
                else sb.append("\r\n");
            }
            String oneLine = sb.toString();
            resWriter.write(oneLine);

        }

        String endProgramTime = dfStart.format(new Date());
        resWriter.write("Program started at " + startProgramTime + " , finished at " + endProgramTime);

        resWriter.flush();
        resWriter.close();
    }

    public void process(int ithTest, long[][] sTime, long[][] time) throws Exception {

        boolean[][] dataSituation = new boolean[dataNum][esNum];
        for(int i =0 ; i < dataNum; i++){
            Arrays.fill(dataSituation[i], true);
        }
        AuditProcess auditProcess = new AuditProcess(BLOCK_NUM, dataSituation, originalPath, replicaPath);

        //KeyGen
        sTime[ithTest][0] = System.nanoTime();
        Key key = auditProcess.keyGen(BLOCK_NUM);

        time[ithTest][0] = System.nanoTime();
        time[ithTest][0] = time[ithTest][0] - sTime[ithTest][0];


        //OutSource
        sTime[ithTest][1] = System.nanoTime();
        auditProcess.outSource();

        time[ithTest][1] = System.nanoTime();
        time[ithTest][1] = time[ithTest][1] - sTime[ithTest][1];

        //Audit
        sTime[ithTest][2] = System.nanoTime();
        ChallengeData[] challengeDatas = auditProcess.audit(esNum, key);

        time[ithTest][2] = System.nanoTime();
        time[ithTest][2] = time[ithTest][2] - sTime[ithTest][2];

        //Prove
        ProofData[] proofDatas = new ProofData[esNum];

        for(int j =0 ; j < esNum; j++){
            byte[] data = AuditProcess.readFile(replicaPath);
            long start = System.nanoTime();
            proofDatas[j] = auditProcess.prove(j, data, challengeDatas[j]);
            long end = System.nanoTime();
            time[ithTest][3] = time[ithTest][3] + (end - start);
        }
        time[ithTest][3] = time[ithTest][3] / esNum;

        //Verify
        sTime[ithTest][4] = System.nanoTime();
        boolean isDataIntact = auditProcess.verify(proofDatas);

        time[ithTest][4] = System.nanoTime();
        time[ithTest][4] = time[ithTest][4] - sTime[ithTest][4];

        if (isDataIntact) {
            System.out.println("In " + ithTest + "th audit ,the data is intact in all edge server");
        } else System.out.println("In " + ithTest + "th audit ,the data is corrupted in  edge server");
    }
}
