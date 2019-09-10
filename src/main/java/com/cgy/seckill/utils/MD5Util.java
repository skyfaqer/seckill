package com.cgy.seckill.utils;

import org.apache.commons.codec.digest.DigestUtils;

// Password encryption:
// Step 1: at client's end, encrypt password with fixed salt, then submit form
// Step 2: at server's end, receive the encrypted password and encrypt again with random salt, then save to database
public class MD5Util {

    private static final String CLIENT_SALT = "1a2b3c4d";

    public static String generateMD5(String src) {
        return DigestUtils.md5Hex(src);
    }

    // Step 1
    public static String inputPassword2FormPassword(String inputPassword) {
        String str = "" + CLIENT_SALT.charAt(0) + CLIENT_SALT.charAt(2) + inputPassword + CLIENT_SALT.charAt(5) + CLIENT_SALT.charAt(4);
        return generateMD5(str);
    }

    // Step 2
    public static String formPassword2DatabasePassword(String formPassword, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPassword + salt.charAt(5) + salt.charAt(4);
        return generateMD5(str);
    }

    // Combined
    public static String inputPassword2DatabasePassword(String inputPassword, String databaseSalt) {
        String formPassword = inputPassword2FormPassword(inputPassword);
        String databasePassword = formPassword2DatabasePassword(formPassword, databaseSalt);
        return databasePassword;
    }
}
