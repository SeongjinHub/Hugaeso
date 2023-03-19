package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signInBtn = (Button) findViewById(R.id.buttonSignIn);
        Button signUpBtn = (Button) findViewById(R.id.buttonSignUp);

        EditText editTextUsername = (EditText) findViewById(R.id.editTextUserName);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword1);

        Drawable editTextNormalUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner);
        Drawable editTextErrorUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner_err);

        Intent intentMainActivity = new Intent(LoginActivity.this, MainActivity.class);

        //SharePreference Key-Value 쌍 비교를 통해 로그인 정보를 불러옴
        SharedPreferences pref = getSharedPreferences("UserName", Activity.MODE_PRIVATE);

        User.username = pref.getString("UserName", "");

        if (User.username.length() == 0) {
            Log.d("Authentication failed. authInfo = ", User.username);

            signInBtn.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.VISIBLE);
            editTextUsername.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);

            editTextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        editTextUsername.setBackground(editTextNormalUI);
                    }
                }
            });

            editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        editTextPassword.setBackground(editTextNormalUI);
                    }
                }
            });

            signInBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    User.username = editTextUsername.getText().toString();
                    User.password = editTextPassword.getText().toString();

                    if (!User.chkUserNameLength(User.username)) {
                        editTextUsername.setBackground(editTextErrorUI);
                    }
                    else if (!User.chkPasswordLength(User.password)) {
                        editTextPassword.setBackground(editTextErrorUI);
                    }
                    else {
                        boolean dbAuthResult = true;
                        //boolean dbAuthResult = false;

                        //DB서버 내 사용자 정보와 비교하는 과정 코드가 필요함
                        //인증 값이 참일 경우
                        if (dbAuthResult) {
                            //인증값이 참일 경우 로컬 앱 데이터에 Username을 저장한 후 메인 액티비티로 이동
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("UserName", User.username);
                            editor.commit();
                            startActivity(intentMainActivity);
                            finish();
                        }
                        else {
                            editTextPassword.setBackground(editTextErrorUI);
                        }
                    }
                }
            });

            signUpBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSignUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intentSignUpActivity);
                }
            });
        } else {
            //이전에 로그인 한 기록이 있을 경우 메인 액티비티로 바로 이동
            Log.d("Authentication succeeded. userName = ", String.valueOf(User.username));
            startActivity(intentMainActivity);
            finish();
        }
    }
}