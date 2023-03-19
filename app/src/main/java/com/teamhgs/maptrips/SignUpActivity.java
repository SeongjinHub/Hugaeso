package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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
                else if (!User.chkuserNameDB(User.username)) {
                    editTextUsername.setBackground(editTextErrorUI);
                    textViewUsernameSub1.setTextColor(defaultTextColor);
                    textViewUsernameSub2.setTextColor(defaultTextColor);
                    textViewUsernameSub3.setTextColor(errColor);
                }
                else {
                    editTextUsername.setBackground(editTextNormalUI);
                    textViewUsernameSub1.setTextColor(defaultTextColor);
                    textViewUsernameSub2.setTextColor(defaultTextColor);
                    textViewUsernameSub3.setTextColor(correctColor);
                    id = true;
                }
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
                }
                else {
                    editTextPassword1.setBackground(editTextNormalUI);
                    textViewPasswordSub1.setTextColor(defaultTextColor);
                }

                if (!User.password.equals(password2)) {
                    editTextPassword2.setBackground(editTextErrorUI);
                    textViewPasswordSub2.setTextColor(errColor);
                }
                else {
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
                }
                else {
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
                }
                else {
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
                }
                else if (!User.isValidEmailAddress(User.email)) {
                    editTextEmail.setBackground(editTextErrorUI);
                    textViewEmaiilSub.setVisibility(View.VISIBLE);
                }
                else {
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
                    //테스트 용
                    Intent intentMainActivity = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intentMainActivity);

                }
            }
        });

    }
}