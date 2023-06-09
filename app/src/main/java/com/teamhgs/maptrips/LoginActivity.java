package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.Permission;

public class LoginActivity extends AppCompatActivity {

    boolean init = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Init();

        setContentView(R.layout.activity_login);

        Button buttonSignIn = (Button) findViewById(R.id.button_signin);
        Button buttonSignUp = (Button) findViewById(R.id.button_signup);
        Button buttonForgot = (Button) findViewById(R.id.button_forgot);

        EditText editTextUsername = (EditText) findViewById(R.id.et_username);
        EditText editTextPassword = (EditText) findViewById(R.id.et_password);

        LinearLayout parent_area = (LinearLayout) findViewById(R.id.parent_area);
        LinearLayout title_area = (LinearLayout) findViewById(R.id.title_area);

        Drawable editTextNormalUI = getResources().getDrawable(R.drawable.edit_rad_corner_sign);
        Drawable editTextErrorUI = getResources().getDrawable(R.drawable.edit_rad_corner_sign_err);

        Animation fade_in = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in_login);
        Animation fade_out = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_out);

        TextView loginSub = (TextView)  findViewById(R.id.text_login_sub);

        Intent intentMainActivity = new Intent(LoginActivity.this, MainActivity.class);


        // SharedPreference Key-Value 쌍 비교를 통해 로그인 정보를 불러옴
        SharedPreferences pref = getSharedPreferences("com.teamhgs.maptrips.user", Activity.MODE_PRIVATE);

        User currentUser = new User(pref.getString("UserCode", ""));

        // 이전의 로그인 기록이 없는 경우 (SharedPref에 값이 없는 경우)
        if (currentUser.getUsercode().length() == 0) {
            Log.d("Authentication failed. authInfo = ", currentUser.getUsercode());

            getWindow().setStatusBarColor(getResources().getColor(R.color.app_main_color));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

            parent_area.setVisibility(View.VISIBLE);
            title_area.startAnimation(AnimationUtils.loadAnimation(LoginActivity.this, R.anim.slide_btm_enter_login));

            buttonSignIn.startAnimation(fade_in);
            buttonForgot.startAnimation(fade_in);
            buttonSignUp.startAnimation(fade_in);
            editTextUsername.startAnimation(fade_in);
            editTextPassword.startAnimation(fade_in);

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

            buttonSignIn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    currentUser.setUsername(editTextUsername.getText().toString());
                    currentUser.setPassword(editTextPassword.getText().toString());

                    if (!currentUser.chkUsernameRegEx()) {
                        loginSub.setVisibility(View.VISIBLE);
                        editTextUsername.setBackground(editTextErrorUI);
                    }
                    else if (!currentUser.chkPasswordRegEx()) {
                        loginSub.setVisibility(View.VISIBLE);
                        editTextPassword.setBackground(editTextErrorUI);
                    }
                    else {

                        try {
                            currentUser.setPassword(User.SHA256.encrypt(currentUser.getUsername() + currentUser.getPassword()));
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }

                        /* Username과 Password가 DB에 저장된 값과 일치하는 지 확인합니다. */
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean loginResult = jsonResponse.getBoolean("responseResult");

                                    if (loginResult) { // 일치할 경우
                                        currentUser.setUsercode(jsonResponse.getString("usercode"));

                                        /* DB에 Usercode, Device ID, DateTime, Auth 정보를 기록합니다. */
                                        Response.Listener<String> responseListener2 = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonResponse2 = new JSONObject(response);
                                                    boolean result = jsonResponse2.getBoolean("responseResult");

                                                    if (result) {
                                                        SharedPreferences.Editor editor = pref.edit();
                                                        editor.putString("UserCode", currentUser.getUsercode()); // 내부 저장소에 Usercode를 저장합니다.
                                                        editor.commit();
                                                        intentMainActivity.putExtra(User.CURRENT_USER, currentUser);
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
                                        User.insertSavedLoginRequest request = new User.insertSavedLoginRequest(currentUser.getUsercode(), DeviceInfoUtil.getDeviceId(getApplicationContext()), 1, responseListener2);
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

                        User.loginRequest Request = new User.loginRequest(currentUser.getUsername(), currentUser.getPassword(), responseListener);
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        queue.add(Request);

                    }
                }
            });

            buttonSignUp.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSignUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
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
                            Log.d("Authentication succeeded. UserCode = ", currentUser.getUsercode());
                            intentMainActivity.putExtra(User.CURRENT_USER, currentUser);
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

            User.savedLoginRequest Request = new User.savedLoginRequest(currentUser.getUsercode(), DeviceInfoUtil.getDeviceId(getApplicationContext()), 1, responseListener);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(Request);
        }
    }

    private void Init() { // 앱 권한 확인 및 허가 요청.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                init = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION}, 101);
            }
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            init = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            init = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 103);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            init = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 104);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0) {
           if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
               // 권한 거부 시
           }
           else {
               // 권한 허가 시
           }
        }
    }
}