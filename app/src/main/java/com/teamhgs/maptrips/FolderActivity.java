package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class FolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        //For Debug
        Toast.makeText(getApplicationContext(), "UserCode = " + User.usercode, Toast.LENGTH_LONG).show();

    }
}