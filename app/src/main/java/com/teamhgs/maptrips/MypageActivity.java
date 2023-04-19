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
        setContentView(R.layout.activity_mypage);
        overridePendingTransition(R.anim.none, R.anim.none);

        User defaultUser = (User) getIntent().getSerializableExtra("defaultUser");

        TextView header_title = (TextView) findViewById(R.id.header_title_text);

        header_title.setText("@"  + defaultUser.getUsername());


        //For Debug
        String temp = defaultUser.getUsercode() + " " + defaultUser.getUsername() + " " + defaultUser.getName() + " " + defaultUser.getEmail();
        Toast.makeText(getApplicationContext(), "Userinfo = " + temp, Toast.LENGTH_LONG).show();

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

        btm_folder_btn.setOnClickListener(new View.OnClickListener() {
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

        ImageButton header_setting_btn = (ImageButton) findViewById(R.id.settingButton);

        header_setting_btn.setOnClickListener(new View.OnClickListener() {
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