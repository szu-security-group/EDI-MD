package com.fchen_group.EDISVMD.Core;

import java.nio.charset.StandardCharsets;

public class Key {
    private byte[] keyHMAC; //The key for HMAC
    private byte[] keyMaster; // The master key

    public Key(String keyHMAC, String keyMaster) {
        this.keyHMAC = keyHMAC.getBytes(StandardCharsets.UTF_8);
        this.keyMaster = keyMaster.getBytes(StandardCharsets.UTF_8);
    }
    public byte[] getKeyHMAC() {return keyHMAC;}
    public byte[] getKeyMaster() {return keyMaster;}

}
