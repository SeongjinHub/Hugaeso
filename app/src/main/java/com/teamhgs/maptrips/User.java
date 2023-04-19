package com.teamhgs.maptrips;

import android.util.Log;
import android.util.Patterns;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class User implements Serializable {

    String usercode;
    String username;
    String password;
    String name;
    String email;
    String country;
    int year;
    int month;
    String deviceinfo;


    private static Map<String, String> parameters;

    public User() {

    }

    public User(String usercode) {
        this.usercode = usercode;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 6-14자리 소문자, 숫자, 특수문자 - _ . 허용
     **/
    public boolean chkUsernameRegEx() {
        String pattern = "[a-z\\d\\-_.]{6,14}";

        if (Pattern.matches(pattern, this.username))
            return true;
        else
            return false;
    }

    /**
     * 8-16자리 문자, 숫자, 특수문자 포함 여부 확인
     **/
    public boolean chkPasswordRegEx() {
        String pattern = "^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=]).*$";

        if (Pattern.matches(pattern, this.password))
            return true;
        else
            return false;
    }

    public boolean chkNameRegEx() {
        String pattern = "[A-Za-z가-힣\\d\\-_.]{2,20}";

        if (Pattern.matches(pattern, this.username))
            return true;
        else
            return false;
    }

    /**
     * Username 중복여부 확인을 위한 PHP POST를 수행
     **/
    public static class chkUsernameRequest extends StringRequest {

        public chkUsernameRequest(String username, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_chk_username.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("username", username);
            } catch (Exception e) {
                Log.d("chkUsernameRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    /**
     * E-mail 규칙확인 Method
     **/
    public boolean isValidEmailAddress() {
        boolean result = true;

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        try {
            if (pattern.matcher(this.email).matches())
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 로그인 시 'PHP'에 'POST' 하기위한 Class 및 Method
     **/
    public static class loginRequest extends StringRequest {

        public loginRequest(String username, String password, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_login.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("username", username);
                parameters.put("password", password);
            } catch (Exception e) {
                Log.d("loginRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    public static class savedLoginRequest extends StringRequest {

        public savedLoginRequest(String usercode, String deviceinfo, int auth, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_saved_login.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
                parameters.put("deviceinfo", deviceinfo);
                parameters.put("auth", String.valueOf(auth));
            } catch (Exception e) {
                Log.d("savedLoginRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    public static class insertSavedLoginRequest extends StringRequest {

        public insertSavedLoginRequest(String usercode, String deviceinfo, int auth, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_saved_login_insert.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
                parameters.put("deviceinfo", deviceinfo);
                parameters.put("auth", String.valueOf(auth));
                parameters.put("datetime", (LocalDate.now() + "." + LocalTime.now()));
            } catch (Exception e) {
                Log.d("insertSavedLoginRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }
}
