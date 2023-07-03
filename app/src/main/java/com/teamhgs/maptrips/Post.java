package com.teamhgs.maptrips;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Post implements Serializable {

    static final String POST = "Post";
    static final int TAG_LAT_LNG = 0;
    static final int TAG_NAME = 1;
    static final int TAG_PLACE_ID = 2;

    String postcode;
    String title;
    String text;
    String date;
    String time;
    String placeID;
    String latitude;
    String longitude;
    int privateStatus;
    ArrayList<String> url = new ArrayList<>();
    private static Map<String, String> parameters;

    public Post() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.postcode = "Post" + format.format(date) + Locale.getDefault().getCountry();

        privateStatus = 0;
    }

    public Post(String postcode) {
        this.postcode = postcode;

        this.privateStatus = 0;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getPrivateStatus() {
        return privateStatus;
    }

    public void setPrivateStatus(int privateStatus) {
        this.privateStatus = privateStatus;
    }

    public ArrayList<String> getUrl() {
        return url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }

    public static class insertPostRequest extends StringRequest {

        public insertPostRequest(String usercode, Post post, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_insert_post.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
                parameters.put("postcode", post.getPostcode());
                parameters.put("title", post.getTitle());
                parameters.put("text", post.getText());
                parameters.put("date", post.getDate());
                parameters.put("placeid", post.getPlaceID());
                parameters.put("latitude", post.getLatitude());
                parameters.put("longitude", post.getLongitude());
                parameters.put("private", String.valueOf(post.getPrivateStatus()));
            } catch (Exception e) {
                Log.d("insertPostRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class insertPostUrlRequest extends StringRequest {

        public insertPostUrlRequest(Post post, String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_insert_post_url.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("postcode", post.getPostcode());
                parameters.put("usercode", usercode);
                parameters.put("url", post.getUrl().get(0));
            } catch (Exception e) {
                Log.d("insertPostUrlRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }

    public static class deletePostRequest extends StringRequest {

        public deletePostRequest(String usercode, Post post, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_delete_post.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
                parameters.put("postcode", post.getPostcode());
                parameters.put("title", post.getTitle());
                parameters.put("text", post.getText());
                parameters.put("date", post.getDate());
                parameters.put("placeid", post.getPlaceID());
                parameters.put("latitude", post.getLatitude());
                parameters.put("longitude", post.getLongitude());
                parameters.put("private", String.valueOf(post.getPrivateStatus()));

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String del_time = format.format(date) + Locale.getDefault().getCountry();

                parameters.put("del_time", del_time);
            } catch (Exception e) {
                Log.d("deletePostRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class getPostsListRequest extends StringRequest {

        public getPostsListRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_mypage_posts_list.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getPostsListRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class getPostsUrlListRequest extends StringRequest {

        public getPostsUrlListRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_mypage_posts_url_list.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getPostUrlListRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class getPostRequest extends StringRequest {

        public getPostRequest(String postcode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_get_post.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("postcode", postcode);
            } catch (Exception e) {
                Log.d("getPostRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class getPostLikeRequest extends StringRequest {

        public getPostLikeRequest(String postcode, String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_get_post_like.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("postcode", postcode);
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getPostLikeRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class insertPostLikeRequest extends StringRequest {

        public insertPostLikeRequest(String postcode, String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_insert_post_like.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("postcode", postcode);
                parameters.put("usercode", usercode);

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String datetime = format.format(date) + Locale.getDefault().getCountry();

                parameters.put("datetime", datetime);
            } catch (Exception e) {
                Log.d("insertPostLikeRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class deletePostLikeRequest extends StringRequest {

        public deletePostLikeRequest(String postcode, String usercode, String datetime, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_delete_post_like.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("postcode", postcode);
                parameters.put("usercode", usercode);
                parameters.put("datetime", datetime);

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String del_time = format.format(date) + Locale.getDefault().getCountry();
                parameters.put("del_time", del_time);
            } catch (Exception e) {
                Log.d("deletePostLikeRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class getBookmarkRequest extends StringRequest {

        public getBookmarkRequest(String objectcode, String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_get_bookmark.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("objectcode", objectcode);
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getBookmarkRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class insertBookmarkRequest extends StringRequest {

        public insertBookmarkRequest(String objectcode, String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_insert_bookmark.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("objectcode", objectcode);
                parameters.put("usercode", usercode);

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String datetime = format.format(date) + Locale.getDefault().getCountry();

                parameters.put("datetime", datetime);
            } catch (Exception e) {
                Log.d("insertBookmarkRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class deleteBookmarkRequest extends StringRequest {

        public deleteBookmarkRequest(String objectcode, String usercode, String datetime, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_delete_bookmark.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("objectcode", objectcode);
                parameters.put("usercode", usercode);
                parameters.put("datetime", datetime);

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String del_time = format.format(date) + Locale.getDefault().getCountry();
                parameters.put("del_time", del_time);
            } catch (Exception e) {
                Log.d("deleteBookmarkRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }
}
