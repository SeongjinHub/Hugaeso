package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
        SharedPreferences pref = getSharedPreferences("UserCode", Activity.MODE_PRIVATE);

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

                    if (!User.chkUserNameLength(User.username)) {
                        loginSub.setVisibility(View.VISIBLE);
                        editTextUsername.setBackground(editTextErrorUI);
                    }
                    else if (!User.chkPasswordLength(User.password)) {
                        loginSub.setVisibility(View.VISIBLE);
                        editTextPassword.setBackground(editTextErrorUI);
                    }
                    else {
                        Response.Listener<String>   responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean loginResult = jsonResponse.getBoolean("loginResult");
                                    if (loginResult) {
                                        User.usercode = jsonResponse.getString("usercode");
//                                        User.username = jsonResponse.getString("username");
//                                        User.name = jsonResponse.getString("name");
//                                        User.email = jsonResponse.getString("email");
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("UserCode", User.usercode); // SharedPref (Local) 에 User.usercode 저장
                                        editor.commit();
                                        startActivity(intentMainActivity); // 메인화면으로 이동
                                        finish(); // 뒤로가기를 통해 LoginActivity 재 접근 차단
                                    }
                                    else {
                                        loginSub.setVisibility(View.VISIBLE); // 로그인 오류 안내 출력
                                        editTextPassword.setBackground(editTextErrorUI);
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };

                        User.loginRequest Request = new User.loginRequest(User.username, User.password ,responseListener);
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        queue.add(Request);

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
            Log.d("Authentication succeeded. UserCode = ", String.valueOf(User.usercode));
            startActivity(intentMainActivity);
            finish();
        }
    }
}