/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.utils;

/**
 * Created by subrat on 15/3/16.
 */
import com.iotpot.server.pojos.constants.IoTPotConstants;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Arrays;
import java.util.Properties;

public class Utils {

    static Logger log = Logger.getLogger(Utils.class);
    private static java.util.Properties serverConf;
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String readValueOf(String attribute) throws IOException {

        if(serverConf == null){
            InputStream inputStream = Properties.class.getResourceAsStream("/application.properties");
            if (inputStream == null) {
                throw new FileNotFoundException("property file not found in the classpath, \nMost probably -DSERVER_ENVIRONMENT variable is not set, please start server by passing proper SERVER_ENVIRONMENT vraible");
            }
            serverConf = new java.util.Properties();
            serverConf.load(inputStream);
        }
        return serverConf.getProperty(attribute).trim();
    }

    private static final String keyAlgorithm = "AES";
    private static final String cipherAlgorithm = "AES/CBC/NoPadding";

    public static byte[] aesEncrypt(String plainText,String encryptKey) throws UnsupportedEncodingException {

        byte[] iv;
        String encryptedValue = null;
        Cipher cipher;
        try {
            Key key = new SecretKeySpec(encryptKey.getBytes(), keyAlgorithm);
            System.out.println("..."+key.getAlgorithm()+"  "+key.getEncoded()+"  "+key.getEncoded().length);
            cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            iv = cipher.getIV();
            byte[] plainTextAfterPadding = addPadding(plainText,16);
            log.info("plainText :" + Arrays.toString(plainText.getBytes()));
            log.info("plainTextAfterPadding :" + Arrays.toString(plainTextAfterPadding));
            //            log.info("iv :"+Arrays.toString(iv));
            byte[] encVal = cipher.doFinal(plainTextAfterPadding);
            //            log.info("encVal :"+Arrays.toString(encVal));
            byte[] finalVal = new byte[iv.length + encVal.length];
            System.arraycopy(iv, 0, finalVal, 0, iv.length);
            System.arraycopy(encVal, 0, finalVal, iv.length, encVal.length);
            //            log.info("finalVal :"+Arrays.toString(finalVal));
            return finalVal;
        } catch (Exception ex) {
            log.error("Wrong command received: " + ex, ex);
            ex.printStackTrace();
            throw new UnsupportedEncodingException("Stun command could not be encoded.");
        }
    }


    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String hexToString(String hex) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static byte[] addPadding(String input,int paddingMultiple) {
        if (input == null) {
            return null;
        }
        int l = (input.length() / paddingMultiple);
        int r = input.length() % paddingMultiple;
        if (r == 0) {
            return input.getBytes();
        }
        byte[] out = new byte[(l + 1) * paddingMultiple];
        System.arraycopy(input.getBytes(), 0, out, 0, input.getBytes().length);
        int paddingLength = out.length - input.length();
        for (int i = 0; i < paddingLength; ++i) {
            out[input.length() + i] = 0;
        }
        return out;
    }


    public static byte[] aesDecrypt(byte[] encryptedBytes, byte[] keyValue) throws UnsupportedEncodingException {
        // generate key
        log.info("got input to decrypt :" + Hex.encodeHexString(encryptedBytes));
        String decryptedValue = null;
        try {
            byte[] ivFromMessage = new byte[16];
            byte[] encryptedMessage = new byte[encryptedBytes.length - 16];
            System.arraycopy(encryptedBytes, 0, ivFromMessage, 0, 16);
            System.arraycopy(encryptedBytes, 16, encryptedMessage, 0, encryptedBytes.length - 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyValue, keyAlgorithm);
            Cipher chiper = Cipher.getInstance(cipherAlgorithm);
            IvParameterSpec ivSpec = new IvParameterSpec(ivFromMessage);
            chiper.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] decValue = chiper.doFinal(encryptedMessage);
            return decValue;
        } catch (Exception ex) {
            log.error("Wrong encrypted text received: " + Hex.encodeHexString(encryptedBytes), ex);
            throw new UnsupportedEncodingException("Stun could not decrupt message.");
        }
    }


    public static String getRemoteIP(HttpServletRequest request){
        /*
         * Get client IP , according to AWS :http://docs.aws.amazon.com/ElasticLoadBalancing/latest/DeveloperGuide/x-forwarded-headers.html
         * client IP is available in 'X-Forwarded-For' header, so first we check that header,
         * if it is null we put request.getremoteip
         */
        String clientRemoteIp=request.getHeader(IoTPotConstants.ELB_CLIENT_IP_HEADER);
        if(StringUtils.isBlank(clientRemoteIp)){
        /*
         * This indicates, not behind ELB
         */
            clientRemoteIp=request.getRemoteAddr();
        }
        else{
         /* if Ip is behind multiple proxy, 'X-Forwarded-For' will have multiple values separated by comma,
          * so we have to take correct one from comman separated list
          */
            clientRemoteIp = clientRemoteIp.split(",")[0];
        }
        return  clientRemoteIp;

    }


    public static void main(String args[]) throws UnsupportedEncodingException {

        long t1= System.currentTimeMillis();
        byte[] e=aesEncrypt("HelloMello","0066123456789013");
        long t2= System.currentTimeMillis();
        byte[] d=aesDecrypt(e,"0066123456789013".getBytes());
        long t3= System.currentTimeMillis();
        System.out.println("Encrypted hex :"+org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(e));
        System.out.println("Decrypted hex :"+new String(d));
        System.out.println("Time taken :"+(t2-t1)+"  "+(t3-t2));


        System.out.println("01006612345678901312312313".substring(2,18));

        String encrypted = "zquc4zzc1QhXlrKBaH6adxSVCb7ZdUL1LvEiiFsyYiE";
        byte[] de = aesDecrypt(org.apache.commons.codec.binary.Base64.decodeBase64(encrypted),"0066123456789013".getBytes());
        System.out.println("Decrypted hex :"+new String(de));







    }

}