package com.teamhgs.maptrips;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Folder implements Serializable {

    String foldercode;
    String title;
    String text;
    String startDate;
    String endDate;
    String updateDate;
    int privateStatus;

    private static Map<String, String> parameters;

    public Folder() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.foldercode = "Folder" + format.format(date) + Locale.getDefault().getCountry();

        privateStatus = 0;
    }

    public Folder(String foldercode) {
        this.foldercode = foldercode;

        privateStatus = 0;
    }

    public String getFoldercode() {
        return foldercode;
    }

    public void setFoldercode(String foldercode) {
        this.foldercode = foldercode;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getPrivateStatus() {
        return privateStatus;
    }

    public void setPrivateStatus(int privateStatus) {
        this.privateStatus = privateStatus;
    }

    public static class getFolderRequest extends StringRequest {

        public getFolderRequest(String usercode, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_get_folder.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("usercode", usercode);
            } catch (Exception e) {
                Log.d("getfolderRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }

    public static class insertFolderPostsRequest extends StringRequest {

        public insertFolderPostsRequest(Folder folder, String postcode, String postDate, Response.Listener<String> listener) {
            super(Method.POST, DB_Framework.IP_ADDRESS + "/db_insert_folder_posts.php", listener, null);
            parameters = new HashMap<>();
            try {
                parameters.put("foldercode", folder.getFoldercode());
                parameters.put("postcode", postcode);

                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                Date PostDate = new Date();
                Date StartDate = new Date();
                Date EndDate = new Date();

                PostDate = formatDate.parse(postDate);
                StartDate = formatDate.parse(folder.getStartDate());
                EndDate = formatDate.parse(folder.getEndDate());

                if (PostDate.compareTo(StartDate) < 0) { // 게시글 날짜가 폴더 시작일 보다 작을 경우
                    parameters.put("startdate", postDate);
                }
                else {
                    parameters.put("startdate", folder.getStartDate());
                }

                if (PostDate.compareTo(EndDate) > 0) {
                    parameters.put("enddate", postDate);
                }
                else {
                    parameters.put("enddate", folder.getEndDate());
                }

                Date date = new Date();
                SimpleDateFormat formatDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                String datetime = formatDateTime.format(date) + Locale.getDefault().getCountry();

                parameters.put("datetime", datetime);
            } catch (Exception e) {
                Log.d("insertPostLikeRequest", "parameter put error");
            }
        }

        protected Map<String, String> getParams() throws AuthFailureError {

            return parameters;
        }
    }
}
