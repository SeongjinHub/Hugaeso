package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signInBtn = (Button) findViewById(R.id.buttonSignIn);
        Button signUpBtn = (Button) findViewById(R.id.buttonSignUp);
        Button forgotBtn = (Button) findViewById(R.id.buttonForgot);

        EditText editTextUsername = (EditText) findViewById(R.id.editTextUserName);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword1);

        Drawable editTextNormalUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner);
        Drawable editTextErrorUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner_err);

        TextView loginSub = (TextView)  findViewById(R.id.textViewLoginSub);

        Intent intentMainActivity = new Intent(LoginActivity.this, MainActivity.class);

        // SharedPreference Key-Value 쌍 비교를 통해 로그인 정보를 불러옴
        SharedPreferences pref = getSharedPreferences("com.teamhgs.maptrips.user", Activity.MODE_PRIVATE);

        User.username = pref.getString("UserCode", "");

        // 이전의 로그인 기록이 없는 경우 (SharedPref에 값이 없는 경우)
        if (User.username.length() == 0) {
            Log.d("Authentication failed. authInfo = ", User.usercode);

            signInBtn.setVisibility(View.VISIBLE); //로그인 및 회원가입을 위한 UI 출력
            signUpBtn.setVisibility(View.VISIBLE);
            forgotBtn.setVisibility(View.VISIBLE);

            editTextUsername.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);

            // EditText 입력 값 변동 시 UI 초기화
            editTextUsername.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    loginSub.setVisibility(View.GONE); // 로그인 오류 안내 숨기기
                    editTextUsername.setBackground(editTextNormalUI); // EditText Style 초기화

                }
            });

            editTextPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    loginSub.setVisibility(View.GONE);
                    editTextPassword.setBackground(editTextNormalUI);
                }
            });

            signInBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    User.username = editTextUsername.getText().toString();
                    User.password = editTextPassword.getText().toString();

                    if (!User.chkUsernameRegEx(User.username)) {
                        loginSub.setVisibility(View.VISIBLE);
                        editTextUsername.setBackground(editTextErrorUI);
                    }
                    else if (!User.chkPasswordRegEx(User.password)) {
                        loginSub.setVisibility(View.VISIBLE);
                        editTextPassword.setBackground(editTextErrorUI);
                    }
                    else {

                        /* Username과 Password가 DB에 저장된 값과 일치하는 지 확인합니다. */
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean loginResult = jsonResponse.getBoolean("responseResult");

                                    if (loginResult) { // 일치할 경우
                                        User.usercode = jsonResponse.getString("usercode");
//                                        User.username = jsonResponse.getString("username");
//                                        User.name = jsonResponse.getString("name");
//                                        User.email = jsonResponse.getString("email");

                                        /* DB에 Usercode, Device ID, DateTime, Auth 정보를 기록합니다. */
                                        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonResponse2 = new JSONObject(response);
                                                    boolean result = jsonResponse2.getBoolean("responseResult");

                                                    if (result) {
                                                        SharedPreferences.Editor editor = pref.edit();
                                                        editor.putString("UserCode", User.usercode); // 내부 저장소에 Usercode를 저장합니다.
                                                        editor.commit();
                                                        startActivity(intentMainActivity); // 메인화면으로 이동
                                                        finish(); // 뒤로가기를 통해 LoginActivity 재 접근 차단
                                                    }
                                                    else {

                                                    }
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        };
                                        User.insertSavedLoginRequest request = new User.insertSavedLoginRequest(User.usercode, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), 1, responseListener2);
                                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                                        queue.add(request);
                                    }
                                    else {
                                        loginSub.setVisibility(View.VISIBLE); // 로그인 오류 안내를 출력합니다.
                                        editTextPassword.setBackground(editTextErrorUI);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };

                        User.loginRequest Request = new User.loginRequest(User.username, User.password, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        queue.add(Request);

                    }
                }
            });

            signUpBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSignUpActivity = new Intent(LoginActivity.this, SignUpActivityV2.class);
                    startActivity(intentSignUpActivity);
                }
            });
        } else {
            /* Usercode와 Device ID값을 DB에 검색하여 인증값(Auth)이 참인지 확인합니다.
             * Value of "auth"
             * 1 - true
             * 2 - false
             * 인증에 실패하면 내부 저장공간에 저장된 Usercode 값을 초기화 하고 액티비티를 재실행합니다. */
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean result = jsonResponse.getBoolean("responseResult");
                        if(result) {
                            Log.d("Authentication succeeded. UserCode = ", String.valueOf(User.usercode));
                            startActivity(intentMainActivity);
                            finish();
                        }
                        else {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("UserCode", "");
                            editor.commit();
                            for (int i = 0; i < 1; i++) {
                                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                finish();
                            }

                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            User.savedLoginRequest Request = new User.savedLoginRequest(User.usercode, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), 1, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(Request);
        }
    }
}