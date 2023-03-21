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
    static int year;
    static int month;
    static String usercode = new String();


    private static Map<String, String> parameters;

    /** Username 길이 검증 Method **/
    public static boolean chkUserNameLength(String username) {
        if (username.length() < 6 || username.length() > 14)
            return false;

        return true;
    }

    /** Password 길이 검증 Method **/
    public static boolean chkPasswordLength(String password) {
        if (password.length() < 8 || password.length() > 16)
            return false;

        return true;
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
