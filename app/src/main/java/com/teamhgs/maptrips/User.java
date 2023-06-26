package com.teamhgs.maptrips;

import android.util.Log;
import android.util.Patterns;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class User implements Serializable {

    static final String CURRENT_USER = "currentUser";
    static final String POST_WRITER = "postWriter";
    String usercode;
    String username;
    String password;
    String name;
    String email;
    String country;
    int year;
    int month;
    String deviceinfo;
    String img;


    private static Map<String, String> parameters;

    public User() { }

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public static class SHA256 {

        public static String encrypt(String text) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

            return bytesToHex(md.digest());
        }

        private static String bytesToHex(byte[] bytes) {
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
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
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_insert_saved_login.php", listener, null);
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

    public static class getUserInfoRequest extends StringRequest {

        public getUserInfoRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_get_user_info.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getUserInfoRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    public static class getPostsCountRequest extends StringRequest {

        public getPostsCountRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_mypage_posts_count.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getPostsCount", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    public static class getFollowerCountRequest extends StringRequest {

        public getFollowerCountRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_mypage_follower_count.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getPostsCount", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    public static class getFollowingCountRequest extends StringRequest {

        public getFollowingCountRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_mypage_following_count.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getPostsCount", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }
}
