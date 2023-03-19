package com.teamhgs.maptrips;

import android.util.Patterns;

import java.util.regex.Pattern;

public class User {

    static String username = new String();
    static String password = new String();
    static String name = new String();
    static String email = new String();
    static int year;
    static int month;
    static int usercode;

    public static boolean chkUserNameLength(String username) {
        if (username.length() < 6 || username.length() > 14)
            return false;

        return true;
    }

    public static boolean chkPasswordLength(String password) {
        if (password.length() < 8 || password.length() > 16)
            return false;

        return true;
    }

    public static boolean chkuserNameDB(String username) {

        // 테스트 용
        if (username.equals("dddddd")) {
            return false;
        }
        return true;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        try {
            if (pattern.matcher(email).matches())
                return true;
            else
                return false;
        }
        catch (Exception e) {
            return false;
        }
    }

}
