package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MypageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_mypage);
        overridePendingTransition(R.anim.none, R.anim.none);

        User defaultUser = (User) getIntent().getSerializableExtra("defaultUser");

        TextView header_title = (TextView) findViewById(R.id.text_header_title);

        header_title.setText("@" + defaultUser.getUsername());


        //For Debug
        String temp = defaultUser.getUsercode() + " " + defaultUser.getUsername() + " " + defaultUser.getName() + " " + defaultUser.getEmail();
        Toast.makeText(getApplicationContext(), "Userinfo = " + temp, Toast.LENGTH_LONG).show();

        Button buttonFeedTab = (Button) findViewById(R.id.button_feed);
        Button buttonSearchTab = (Button) findViewById(R.id.button_search);
        Button buttonWriteTab = (Button) findViewById(R.id.button_write);
        Button buttonFolderTab = (Button) findViewById(R.id.button_folder);
        Button buttonMypageTab = (Button) findViewById(R.id.button_mypage);

        buttonFeedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, FeedActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

        buttonSearchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, SearchActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

        buttonWriteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, WriteActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
//                finish();
            }
        });

        buttonFolderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, FolderActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

//        btm_mypage_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MypageActivity.this, MypageActivity.class);
//                intent.putExtra("defaultUser", defaultUser);
//                startActivity(intent);
//                finish();
//            }
//        });

        ImageButton buttonSetting = (ImageButton) findViewById(R.id.button_setting);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getSharedPreferences("com.teamhgs.maptrips.user", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("UserCode", "");
                editor.commit();

                startActivity(new Intent(MypageActivity.this, LoginActivity.class));
                finish();

            }
        });
    }
}