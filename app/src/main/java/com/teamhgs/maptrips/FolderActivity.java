package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_folder);
        overridePendingTransition(R.anim.none, R.anim.none);

        User currentUser = (User) getIntent().getSerializableExtra(User.CURRENT_USER);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");

                    if (result) {
                        currentUser.setUsername(jsonResponse.getString("username"));
                        currentUser.setName(jsonResponse.getString("name"));
                        currentUser.setEmail(jsonResponse.getString("email"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        User.getUserInfoRequest request = new User.getUserInfoRequest(currentUser.getUsercode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(FolderActivity.this);
        queue.add(request);

        Button buttonFeedTab = (Button) findViewById(R.id.button_feed);
        Button buttonSearchTab = (Button) findViewById(R.id.button_search);
        Button buttonWriteTab = (Button) findViewById(R.id.button_write);
        Button buttonFolderTab = (Button) findViewById(R.id.button_folder);
        Button buttonMypageTab = (Button) findViewById(R.id.button_mypage);

        buttonFeedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderActivity.this, FeedActivity.class);
                intent.putExtra(User.CURRENT_USER, currentUser);
                startActivity(intent);
                finish();
            }
        });

        buttonSearchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderActivity.this, SearchActivity.class);
                intent.putExtra(User.CURRENT_USER, currentUser);
                startActivity(intent);
                finish();
            }
        });

        buttonWriteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderActivity.this, WriteActivity.class);
                intent.putExtra(User.CURRENT_USER, currentUser);
                startActivity(intent);
//                finish();
            }
        });

//        buttonFolderTab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FolderActivity.this, FolderActivity.class);
//                intent.putExtra(User.CURRENT_USER, currentUser);
//                startActivity(intent);
//                finish();
//            }
//        });

        buttonMypageTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FolderActivity.this, MypageActivity.class);
                intent.putExtra(User.CURRENT_USER, currentUser);
                startActivity(intent);
                finish();
            }
        });

    }
}