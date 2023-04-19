package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        overridePendingTransition(R.anim.none, R.anim.none);

        User defaultUser = (User) getIntent().getSerializableExtra("defaultUser");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");

                    if (result) {
                        defaultUser.setUsername(jsonResponse.getString("username"));
                        defaultUser.setName(jsonResponse.getString("name"));
                        defaultUser.setEmail(jsonResponse.getString("email"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        User.getUserinfoRequest request = new User.getUserinfoRequest(defaultUser.getUsercode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(FolderActivity.this);
        queue.add(request);

        //For Debug
        Toast.makeText(getApplicationContext(), "UserCode = " + defaultUser.getUsercode(), Toast.LENGTH_LONG).show();

        Button btm_feed_btn = (Button) findViewById(R.id.feedButton);
        Button btm_search_btn = (Button) findViewById(R.id.searchButton);
        Button btm_write_btn = (Button) findViewById(R.id.writeButton);
        Button btm_folder_btn = (Button) findViewById(R.id.folderButton);
        Button btm_mypage_btn = (Button) findViewById(R.id.mypageButton);

        btm_feed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btm_search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btm_write_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        btm_folder_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        btm_mypage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderActivity.this, MypageActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });
    }
}