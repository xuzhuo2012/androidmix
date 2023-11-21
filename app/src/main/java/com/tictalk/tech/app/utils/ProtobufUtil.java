package com.tictalk.tech.app.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.zip.GZIPInputStream;
import java.lang.Runnable;

public class ProtobufUtil {
    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1) {
             int char1 = ((bytes[index] >> 4) & 0xF);
             char chara1 = Character.forDigit(char1, 16);
             int char2 = ((bytes[index]) & 0xF);
             char chara2 = Character.forDigit(char2, 16);
             result.append(chara1);
             result.append(chara2);
        }
         return result.toString();
    }

    /**
     * 将 Gzip hexStr 内容解码
     * @param hexStr
     * @return String 解码后的 hexStr
     */
    public static String transfer(String hexStr) {
        byte[] bytes  = ProtobufUtil.decodeHexString(hexStr);
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        try {
            GZIPInputStream ungzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ProtobufUtil.byteArrayToHex(out.toByteArray());
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = ProtobufUtil.hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = ProtobufUtil.toDigit(hexString.charAt(0));
        int secondDigit = ProtobufUtil.toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    public static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

}