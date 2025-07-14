package com.fchen_group.EDISVMD.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChallengeData {
    public byte[] keyHMAC;
    public byte[] keyMaster;
    public Map<Integer,byte[]> correctionCoefficients;

    public ChallengeData(){
        correctionCoefficients = new HashMap<Integer,byte[]>();
    }

    public ChallengeData(byte[] keyHMAC, byte[] keyMaster) {
        this.keyHMAC = keyHMAC;
        this.keyMaster = keyMaster;
        correctionCoefficients = new HashMap<Integer,byte[]>();
    }
}
