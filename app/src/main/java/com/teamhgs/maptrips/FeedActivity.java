package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
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
        User.getUserInfoRequest request = new User.getUserInfoRequest(defaultUser.getUsercode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(FeedActivity.this);
        queue.add(request);

        Button buttonFeedTab = (Button) findViewById(R.id.button_feed);
        Button buttonSearchTab = (Button) findViewById(R.id.button_search);
        Button buttonWriteTab = (Button) findViewById(R.id.button_write);
        Button buttonFolderTab = (Button) findViewById(R.id.button_folder);
        Button buttonMypageTab = (Button) findViewById(R.id.button_mypage);

//        buttonFeedTab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FeedActivity.this, FeedActivity.class);
//                intent.putExtra("defaultUser", defaultUser);
//                startActivity(intent);
//                finish();
//            }
//        });

        buttonSearchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, SearchActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

        buttonWriteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, WriteActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
//                finish();
            }
        });

        buttonFolderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, FolderActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

        buttonMypageTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this, MypageActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });
    }
}