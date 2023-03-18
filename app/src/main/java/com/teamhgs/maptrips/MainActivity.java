package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textViewUsername = (TextView) findViewById(R.id.userNameText);

        textViewUsername.setText(User.userName);

    }
}