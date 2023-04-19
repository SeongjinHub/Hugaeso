package com.teamhgs.maptrips;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;

public class SignUpActivity extends AppCompatActivity {

    int step = 0;

    boolean id, pw, name, email = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_main);

        overridePendingTransition(R.anim.none, R.anim.none);

        ViewGroup container = (ViewGroup) findViewById(R.id.container);

        Button buttonCancel = (Button) findViewById(R.id.button_cancel);
        Button buttonConfirm = (Button) findViewById(R.id.button_confirm);

        Drawable editTextNormalUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner);
        Drawable editTextErrorUI = getResources().getDrawable(R.drawable.edittext_login_ui_rounded_corner_err);

        int errColor = ContextCompat.getColor(getApplicationContext(), com.google.android.material.R.color.design_default_color_error);
        int defaultTextColor = ContextCompat.getColor(getApplicationContext(), R.color.default_text_color);
        int correctColor = ContextCompat.getColor(getApplicationContext(), R.color.correct_color);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.activity_sign_up_username, container, true);

        Animation fade_in = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.fade_in);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editTextUsername = (EditText) findViewById(R.id.et_username);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewUsernameSub1 = (TextView) findViewById(R.id.et_username_sub);

        editTextUsername.startAnimation(fade_in);
        textViewUsernameSub1.startAnimation(fade_in);

        User newUser = new User();

        // .addTextChangedListener() 실시간 입력 값 검증
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

                newUser.setUsername(editTextUsername.getText().toString());

                // DB접속을 통해 User.username 검증 (입력여부 및 길이검증 포함)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean usernameNotDup = jsonResponse.getBoolean("chkResult"); //DB에 중복될 시 true 반환

                            if (newUser.getUsername().length() < 1) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_sub1));
                                buttonConfirm.setVisibility(View.INVISIBLE);
                            }
                            else if (!newUser.chkUsernameRegEx()) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_sub2));
                                buttonConfirm.setVisibility(View.INVISIBLE);
                            }
                            else if (usernameNotDup) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_dup)); //중복될 경우
                                buttonConfirm.setVisibility(View.INVISIBLE);
                            }
                            else {
                                editTextUsername.setBackground(editTextNormalUI);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_verified));
                                textViewUsernameSub1.setTextColor(correctColor);
                                id = true;
                                buttonConfirm.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            id = false;
                            e.printStackTrace();
                        }
                    }
                };

                User.chkUsernameRequest Request = new User.chkUsernameRequest(newUser.getUsername(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
                queue.add(Request);

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirm.setVisibility(View.INVISIBLE);
                if (step == 0 && id) {
                    step = 1;

                    container.removeAllViews();
                    layoutInflater.inflate(R.layout.activity_sign_up_password, container, true);
                    Animation anim = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.slide_left_enter);
                    container.startAnimation(anim);

                    EditText editTextPassword1 = (EditText) findViewById(R.id.et_password);
                    EditText editTextPassword2 = (EditText) findViewById(R.id.et_password2);

                    TextView textViewPasswordSub1 = (TextView) findViewById(R.id.text_password_sub1);
                    TextView textViewPasswordSub2 = (TextView) findViewById(R.id.text_password_sub2);

                    editTextPassword1.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            buttonConfirm.setVisibility(View.INVISIBLE);
                            pw = false;

                            newUser.setPassword(editTextPassword1.getText().toString());
                            String password2 = editTextPassword2.getText().toString();

                            if (!newUser.chkPasswordRegEx()) {
                                editTextPassword1.setBackground(editTextErrorUI);
                                textViewPasswordSub1.setTextColor(errColor);
                            } else {
                                editTextPassword1.setBackground(editTextNormalUI);
                                textViewPasswordSub1.setTextColor(defaultTextColor);
                            }

                            if (!newUser.getPassword().equals(password2)) {
                                editTextPassword2.setBackground(editTextErrorUI);
                                textViewPasswordSub2.setTextColor(errColor);
                            } else {
                                editTextPassword2.setBackground(editTextNormalUI);
                                textViewPasswordSub2.setTextColor(defaultTextColor);
                                pw = true;
                                buttonConfirm.setVisibility(View.VISIBLE);
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

                            buttonConfirm.setVisibility(View.INVISIBLE);
                            pw = false;

                            newUser.setPassword(editTextPassword1.getText().toString());
                            String password2 = editTextPassword2.getText().toString();

                            if (!newUser.getPassword().equals(password2) || newUser.getPassword().length() < 1) {
                                editTextPassword2.setBackground(editTextErrorUI);
                                textViewPasswordSub2.setTextColor(errColor);
                            } else {
                                editTextPassword2.setBackground(editTextNormalUI);
                                textViewPasswordSub2.setTextColor(defaultTextColor);
                                pw = true;
                                buttonConfirm.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else if (step == 1 && pw) {
                    step = 2;
                    buttonConfirm.setVisibility(View.INVISIBLE);

                    container.removeAllViews();
                    layoutInflater.inflate(R.layout.activity_sign_up_userinfo, container, true);

                    Animation anim = AnimationUtils.loadAnimation(SignUpActivity.this, R.anim.slide_left_enter);
                    container.startAnimation(anim);

                    EditText editTextName = (EditText) findViewById(R.id.et_name);
                    EditText editTextEmail = (EditText) findViewById(R.id.et_email);

                    TextView textViewNameSub = (TextView) findViewById(R.id.text_name_sub);
                    TextView textViewEmailSub = (TextView) findViewById(R.id.text_email_sub);

                    editTextName.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                            buttonConfirm.setVisibility(View.INVISIBLE);
                            name = false;

                            newUser.setName(editTextName.getText().toString());

                            if (!newUser.chkNameRegEx()) {
                                editTextName.setBackground(editTextErrorUI);
                                textViewNameSub.setVisibility(View.VISIBLE);
                            } else {
                                editTextName.setBackground(editTextNormalUI);
                                textViewNameSub.setVisibility(View.GONE);
                                name = true;
                            }
                            if (name && email)
                                buttonConfirm.setVisibility(View.VISIBLE);
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

                            buttonConfirm.setVisibility(View.INVISIBLE);
                            email = false;

                            newUser.setEmail(editTextEmail.getText().toString());

                            if (newUser.getEmail().length() < 1) {
                                editTextEmail.setBackground(editTextErrorUI);
                                textViewEmailSub.setVisibility(View.VISIBLE);
                            } else if (!newUser.isValidEmailAddress()) {
                                editTextEmail.setBackground(editTextErrorUI);
                                textViewEmailSub.setVisibility(View.VISIBLE);
                            } else {
                                editTextEmail.setBackground(editTextNormalUI);
                                textViewEmailSub.setVisibility(View.GONE);
                                email = true;
                            }
                            if (name && email)
                                buttonConfirm.setVisibility(View.VISIBLE);
                        }
                    });
                } else if (step == 2 && (name && email)) {

                    SignUpActivity.InsertData task = new SignUpActivity.InsertData();

                    newUser.setUsercode("Android");
                    newUser.usercode = newUser.getUsercode() + LocalDate.now() + "." + LocalTime.now();
                    task.execute(DB_Framework.IP_ADDRESS + "/db_signup.php", newUser.usercode, newUser.username, newUser.password, newUser.name, newUser.email);
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(SignUpActivity.this, LoginActivity.class);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.none, R.anim.none);
    }

    /** DB INSERT Query를 수행하는 Class 및 Method **/
    private class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String msg;

            if (s.equals("true")) {
                msg = getResources().getString(R.string.activity_signup_reg_result_true);

                Intent intentMainActivity = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intentMainActivity);
                finish();
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

            try {
                password = User.SHA256.encrypt(username + password);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

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