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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Post {

    String postcode;
    String title;
    String text;
    String date;
    String time;
    String country;
    String city;
    String area;
    int privateStatus;
    String url;

    private static Map<String, String> parameters;

    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReference();
    static StorageReference postsRef = storageRef.child("Posts");

    public Post() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.postcode = "Post" + format.format(date) + Locale.getDefault().getCountry();

        privateStatus = 0;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getPrivateStatus() {
        return privateStatus;
    }

    public void setPrivateStatus(int privateStatus) {
        this.privateStatus = privateStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void uploadStorageStream(String fromFilePath, String newFileName) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(fromFilePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        StorageReference postsImageRef = storageRef.child("Posts/" + getPostcode() + "/" + newFileName);

        UploadTask uploadTask = postsImageRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public static class insertPostRequest extends StringRequest {

        public insertPostRequest(String usercode, Post post, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_post_insert.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
                parameters.put("postcode", post.getPostcode());
                parameters.put("title", post.getTitle());
                parameters.put("text", post.getText());
                parameters.put("date", LocalDate.now().toString());
//                parameters.put("country", post.getCountry());
//                parameters.put("city", post.getCity());
//                parameters.put("area", post.getArea());
                parameters.put("country", "1");
                parameters.put("city", "1");
                parameters.put("area", "1");
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

        public insertPostUrlRequest(Post post, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_post_url_insert.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("postcode", post.getPostcode());
                parameters.put("url", post.getUrl());
            } catch (Exception e) {
                Log.d("insertPostUrlRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }

    }
}
