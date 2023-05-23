package com.teamhgs.maptrips;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Folder implements Serializable {

    String foldercode;
    String title;
    String text;
    String date;
    String country;
    String city;
    String area;
    int privateStatus;
    private static Map<String, String> parameters;


    public Folder() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        this.foldercode = "Folder" + format.format(date) + Locale.getDefault().getCountry();

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrivateStatus() {
        return privateStatus;
    }

    public void setPrivateStatus(int privateStatus) {
        this.privateStatus = privateStatus;
    }
}
