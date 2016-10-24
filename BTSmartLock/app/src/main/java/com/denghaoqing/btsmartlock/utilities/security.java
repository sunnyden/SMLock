package com.denghaoqing.btsmartlock.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by exede on 10/24/2016.
 */
public class security {
    public static String md5(String message) {
        String result="";
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(message.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16) buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result=buf.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
