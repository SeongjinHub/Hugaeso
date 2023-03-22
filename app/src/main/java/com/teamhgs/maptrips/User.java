package com.teamhgs.maptrips;

import android.util.Log;
import android.util.Patterns;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class User {

    static String username = new String();
    static String password = new String();
    static String name = new String();
    static String email = new String();
    static String country = new String();
    static int year;
    static int month;
    static String usercode = new String();


    private static Map<String, String> parameters;

    /** 6-14자리 소문자, 숫자, 특수문자 - _ . 허용 **/
    public static boolean chkUsernameRegEx(String username) {
        String pattern = "[a-z\\d\\-_.]{6,14}";

        if (Pattern.matches(pattern, username))
            return true;
        else
            return false;
    }

    /** 8-16자리 문자, 숫자, 특수문자 포함 여부 확인 **/
    public static boolean chkPasswordRegEx(String password) {
        String pattern = "^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=]).*$";

        if (Pattern.matches(pattern, password))
            return true;
        else
            return false;
    }

    public static boolean chkNameRegEx(String name) {
        String pattern = "[A-Za-z가-힣\\d\\-_.]{2,20}";

        if (Pattern.matches(pattern, name))
            return true;
        else
            return false;
    }

    /** User.Username 중복여부 확인을 위한 PHP POST를 수행 **/
    public static class chkUsernameRequest extends StringRequest {

        public chkUsernameRequest(String username, Response.Listener<String> listener){
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_chk_username.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("username", username);
            }
            catch (Exception e) {
                Log.d("chkUsernameRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    /** E-mail 규칙확인 Method **/
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

    /** 로그인 시 'PHP'에 'POST' 하기위한 Class 및 Method **/
    public static class loginRequest extends StringRequest {

        public loginRequest(String username, String password, Response.Listener<String> listener){
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_login.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("username", username);
                parameters.put("password", password);
            }
            catch (Exception e) {
                Log.d("loginRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

}
