package com.teamhgs.maptrips;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;

public class SignUpActivity extends AppCompatActivity {

    boolean id, pw, name, email, userinfoAgr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText editTextUsername = (EditText) findViewById(R.id.editTextUserName);
        EditText editTextPassword1 = (EditText) findViewById(R.id.editTextPassword1);
        EditText editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);
        EditText editTextName = (EditText) findViewById(R.id.editTextName);
        EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        TextView textViewUsernameSub1 = (TextView) findViewById(R.id.textViewUsernameSub1);
        TextView textViewUsernameSub2 = (TextView) findViewById(R.id.textViewUsernameSub2);
        TextView textViewUsernameSub3 = (TextView) findViewById(R.id.textViewUsernameSub3);

        TextView textViewPasswordSub1 = (TextView) findViewById(R.id.textViewPasswordSub1);
        TextView textViewPasswordSub2 = (TextView) findViewById(R.id.textViewPasswordSub2);

        TextView textViewNameSub = (TextView) findViewById(R.id.textViewNameSub);
        TextView textViewEmaiilSub = (TextView) findViewById(R.id.textViewEmailSub);

        CheckBox checkBoxUserInfoAgr = (CheckBox) findViewById(R.id.checkBoxUserInfoAgr);

        Button buttonSignUp = (Button) findViewById(R.id.buttonSignUp);

        Drawable editTextNormalUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner);
        Drawable editTextErrorUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner_err);

        int errColor = ContextCompat.getColor(getApplicationContext(), com.google.android.material.R.color.design_default_color_error);
        int defaultTextColor = ContextCompat.getColor(getApplicationContext(), R.color.default_text_color);
        int correctColor = ContextCompat.getColor(getApplicationContext(), R.color.correct_color);

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                id = false;

                User.username = editTextUsername.getText().toString();

                textViewUsernameSub3.setText(getResources().getString(R.string.activity_signup_username_verifying));


                //username 중복확인 검사
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean usernameNotDup = jsonResponse.getBoolean("chkResult"); //DB에 중복될 시 true 반환
//
                            if (User.username.length() < 1) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub2.setTextColor(defaultTextColor);
                                textViewUsernameSub3.setTextColor(defaultTextColor);
                            }
                            else if (!User.chkUserNameLength(User.username)) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(defaultTextColor);
                                textViewUsernameSub2.setTextColor(errColor);
                                textViewUsernameSub3.setTextColor(defaultTextColor);
                            }
                            else if (usernameNotDup) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(defaultTextColor);
                                textViewUsernameSub2.setTextColor(defaultTextColor);

                                textViewUsernameSub3.setText(getResources().getString(R.string.activity_signup_username_dup));
                                textViewUsernameSub3.setTextColor(errColor);
                                id = false; //중복될 경우
                            }
                            else {
                                editTextUsername.setBackground(editTextNormalUI);
                                textViewUsernameSub1.setTextColor(defaultTextColor);
                                textViewUsernameSub2.setTextColor(defaultTextColor);

                                textViewUsernameSub3.setText(getResources().getString(R.string.activity_signup_username_verified));
                                textViewUsernameSub3.setTextColor(correctColor);
                                id = true;
                            }
                        } catch (Exception e) {
                            id = false;
                            e.printStackTrace();
                        }
                    }
                };

                User.chkUsernameRequest Request = new User.chkUsernameRequest(User.username, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                queue.add(Request);

            }
        });

        editTextPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                pw = false;

                User.password = editTextPassword1.getText().toString();
                String password2 = editTextPassword2.getText().toString();

                if (!User.chkPasswordLength(User.password)) {
                    editTextPassword1.setBackground(editTextErrorUI);
                    textViewPasswordSub1.setTextColor(errColor);
                } else {
                    editTextPassword1.setBackground(editTextNormalUI);
                    textViewPasswordSub1.setTextColor(defaultTextColor);
                }

                if (!User.password.equals(password2)) {
                    editTextPassword2.setBackground(editTextErrorUI);
                    textViewPasswordSub2.setTextColor(errColor);
                } else {
                    editTextPassword2.setBackground(editTextNormalUI);
                    textViewPasswordSub2.setTextColor(defaultTextColor);
                    pw = true;
                }
            }
        });

        editTextPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                pw = false;

                User.password = editTextPassword1.getText().toString();
                String password2 = editTextPassword2.getText().toString();

                if (!User.password.equals(password2)) {
                    editTextPassword2.setBackground(editTextErrorUI);
                    textViewPasswordSub2.setTextColor(errColor);
                } else {
                    editTextPassword2.setBackground(editTextNormalUI);
                    textViewPasswordSub2.setTextColor(defaultTextColor);
                    pw = true;
                }
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                name = false;

                User.name = editTextName.getText().toString();

                if (User.name.length() < 1) {
                    editTextName.setBackground(editTextErrorUI);
                    textViewNameSub.setVisibility(View.VISIBLE);
                } else {
                    editTextName.setBackground(editTextNormalUI);
                    textViewNameSub.setVisibility(View.GONE);
                    name = true;
                }
            }
        });

        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                email = false;

                User.email = editTextEmail.getText().toString();

                if (User.email.length() < 1) {
                    editTextEmail.setBackground(editTextErrorUI);
                    textViewEmaiilSub.setVisibility(View.VISIBLE);
                } else if (!User.isValidEmailAddress(User.email)) {
                    editTextEmail.setBackground(editTextErrorUI);
                    textViewEmaiilSub.setVisibility(View.VISIBLE);
                } else {
                    editTextEmail.setBackground(editTextNormalUI);
                    textViewEmaiilSub.setVisibility(View.GONE);
                    email = true;
                }
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.username = editTextUsername.getText().toString();
                User.password = editTextPassword1.getText().toString();

                userinfoAgr = checkBoxUserInfoAgr.isChecked();

                if (!id) {
                    editTextUsername.setBackground(editTextErrorUI);
                } else if (!pw) {
                    editTextPassword1.setBackground(editTextErrorUI);
                } else if (!name) {
                    editTextName.setBackground(editTextErrorUI);
                } else if (!email) {
                    editTextEmail.setBackground(editTextErrorUI);
                } else if (!userinfoAgr) {
                    checkBoxUserInfoAgr.setTextColor(errColor);
                } else {

                    InsertData task = new InsertData();

                    User.usercode = "Android";
                    User.usercode = User.usercode + LocalDate.now() + LocalTime.now();
                    task.execute(DB_Framework.IP_ADDRESS + "/db_signup.php", User.usercode, User.username, User.password, User.name, User.email);

                    //테스트 용
//                    Intent intentMainActivity = new Intent(SignUpActivity.this, MainActivity.class);
//                    startActivity(intentMainActivity);

                }
            }
        });

    }

    class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String msg;

            if (s.equals("true")) {
                msg = getResources().getString(R.string.activity_signup_reg_result_true);

                Intent intentMainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intentMainActivity);
            } else {
                msg = getResources().getString(R.string.activity_signup_reg_result_false);
            }

            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {

            String usercode = (String) params[1];
            String username = (String) params[2];
            String password = (String) params[3];
            String name = (String) params[4];
            String email = (String) params[5];

            String serverURL = (String) params[0];
            String postParameters = "usercode=" + usercode + "&username=" + username + "&password=" + password + "&name=" + name + "&email=" + email;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();

                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

}