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
import java.time.LocalDate;
import java.time.LocalTime;

public class SignUpActivityV2 extends AppCompatActivity {

    int step = 0;

    boolean id, pw, name, email = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_main);

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

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText editTextUsername = (EditText) findViewById(R.id.editTextUserName);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewUsernameSub1 = (TextView) findViewById(R.id.textViewUsernameSub1);

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
                buttonConfirm.setVisibility(View.INVISIBLE);

                User.username = editTextUsername.getText().toString();

                // DB접속을 통해 User.username 검증 (입력여부 및 길이검증 포함)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean usernameNotDup = jsonResponse.getBoolean("chkResult"); //DB에 중복될 시 true 반환

                            if (User.username.length() < 1) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_sub1));
                            }
                            else if (!User.chkUsernameRegEx(User.username)) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_sub2));
                            }
                            else if (usernameNotDup) {
                                editTextUsername.setBackground(editTextErrorUI);
                                textViewUsernameSub1.setTextColor(errColor);
                                textViewUsernameSub1.setText(getResources().getString(R.string.activity_signup_username_dup)); //중복될 경우
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

                User.chkUsernameRequest Request = new User.chkUsernameRequest(User.username, responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignUpActivityV2.this);
                queue.add(Request);

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (step == 0 && id) {
                    step = 1;
                    buttonConfirm.setVisibility(View.INVISIBLE);

                    container.removeAllViews();
                    layoutInflater.inflate(R.layout.activity_sign_up_password, container, true);

                    EditText editTextPassword1 = (EditText) findViewById(R.id.editTextPassword1);
                    EditText editTextPassword2 = (EditText) findViewById(R.id.editTextPassword2);

                    TextView textViewPasswordSub1 = (TextView) findViewById(R.id.textViewPasswordSub1);
                    TextView textViewPasswordSub2 = (TextView) findViewById(R.id.textViewPasswordSub2);

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

                            User.password = editTextPassword1.getText().toString();
                            String password2 = editTextPassword2.getText().toString();

                            if (!User.chkPasswordRegEx(User.password)) {
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

                            User.password = editTextPassword1.getText().toString();
                            String password2 = editTextPassword2.getText().toString();

                            if (!User.password.equals(password2)) {
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

                    EditText editTextName = (EditText) findViewById(R.id.editTextName);
                    EditText editTextEmail = (EditText) findViewById(R.id.editTextEmail);

                    TextView textViewNameSub = (TextView) findViewById(R.id.textViewNameSub);
                    TextView textViewEmailSub = (TextView) findViewById(R.id.textViewEmailSub);

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

                            User.name = editTextName.getText().toString();

                            if (!User.chkNameRegEx(User.name)) {
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

                            User.email = editTextEmail.getText().toString();

                            if (User.email.length() < 1) {
                                editTextEmail.setBackground(editTextErrorUI);
                                textViewEmailSub.setVisibility(View.VISIBLE);
                            } else if (!User.isValidEmailAddress(User.email)) {
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

                    SignUpActivityV2.InsertData task = new SignUpActivityV2.InsertData();

                    User.usercode = "Android";
                    User.usercode = User.usercode + LocalDate.now() + "." + LocalTime.now();
                    task.execute(DB_Framework.IP_ADDRESS + "/db_signup.php", User.usercode, User.username, User.password, User.name, User.email);
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLoginActivity = new Intent(SignUpActivityV2.this, LoginActivity.class);
                startActivity(intentLoginActivity);
                finish();
            }
        });
    }

    /** DB INSERT Query를 수행하는 Class 및 Method **/
    private class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String msg;

            if (s.equals("true")) {
                msg = getResources().getString(R.string.activity_signup_reg_result_true);

                Intent intentMainActivity = new Intent(SignUpActivityV2.this, LoginActivity.class);
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