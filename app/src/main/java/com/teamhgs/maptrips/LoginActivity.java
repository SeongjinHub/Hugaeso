package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    String userName;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signInBtn = (Button) findViewById(R.id.buttonSignIn);
        Button signUpBtn = (Button) findViewById(R.id.buttonSignUp);

        EditText editTextUsername = (EditText) findViewById(R.id.editTextUserName);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        Intent intentMainActivity = new Intent(LoginActivity.this, MainActivity.class);

        //SharePreference Key-Value 쌍 비교를 통해 로그인 정보를 불러옴
        SharedPreferences pref = getSharedPreferences("UserName", Activity.MODE_PRIVATE);

        userName = pref.getString("UserName", "");
        password = new String();

        if (userName.length() == 0) {
            Log.d("Authentication failed. authInfo = ", userName);

            signInBtn.setVisibility(View.VISIBLE);
            signUpBtn.setVisibility(View.VISIBLE);
            editTextUsername.setVisibility(View.VISIBLE);
            editTextPassword.setVisibility(View.VISIBLE);


            signInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userName = editTextUsername.getText().toString();
                    password = editTextPassword.getText().toString();

                    // UserName field 와 Password field 의 규칙 검사
                    // 아직 Password 규칙은 미구현
                    if (userName.length() > 5 && userName.length() < 15) {

                        //추후 DB쿼리를 통해 인증 여부를 저장할 변수
                        boolean dbAuthResult = true;
                        //boolean dbAuthResult = false;

                        //DB서버 내 사용자 정보와 비교하는 과정 코드가 필요함
                        //인증 값이 참일 경우
                        if (dbAuthResult == true) {
                            //인증값이 참일 경우 로컬 앱 데이터에 UserName을 저장한 후 메인 액티비티로 이동
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("UserName", userName);
                            editor.commit();
                            startActivity(intentMainActivity);
                            finish();
                        } else {
                            // 현재는 Toast msg 출력으로 구현하였으나 추후 다국어 지원을 위해 UI상으로 구현할 예정 (visiblity 이용)
                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Check Value!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //이전에 로그인 한 기록이 있을 경우 메인 액티비티로 바로 이동
            Log.d("Authentication succeeded. userName = ", String.valueOf(userName));
            startActivity(intentMainActivity);
            finish();
        }
    }
}