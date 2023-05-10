package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MypagePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_post);

        Post currentPost = (Post) getIntent().getSerializableExtra("Post");

        TextView textView = (TextView) findViewById(R.id.textView);

        textView.setText(currentPost.getPostcode() + " " + currentPost.getUrl().get(0));
    }
}