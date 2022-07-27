package com.example.paykeeperfyp.CommonCredential;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.paykeeperfyp.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button next, login;

    TextInputLayout fullName, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        intiWidget();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNextSignupScreen();
            }
        });

    }

    private void callNextSignupScreen() {
        if (!validateFullName() | !validateEmail() | !validatePassword()) {
            return;
        } else {
            Intent intent = new Intent(getApplicationContext(), RegisterActivityTwo.class);
            intent.putExtra("name", fullName.getEditText().getText().toString());
            intent.putExtra("email", email.getEditText().getText().toString());
            intent.putExtra("password", password.getEditText().getText().toString());
            startActivity(intent);
        }
    }

    private void intiWidget() {
        next = findViewById(R.id.signup_next_btn);
        login = findViewById(R.id.signup_login_btn);
        //Hooks for Fields
        fullName = findViewById(R.id.et_fullname_reg);
        email = findViewById(R.id.et_email_reg);
        password = findViewById(R.id.et_userpass_reg);
    }

    public void gotoLoginActivity(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private boolean validateFullName() {
        String value = fullName.getEditText().getText().toString();
        if (value.isEmpty()) {
            fullName.setError("Field Cannot be Empty");
            return false;
        } else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String value = email.getEditText().getText().toString();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";  //From Capital A-z and where w is whitespaces
        if (value.isEmpty()) {
            email.setError("Field Cannot be Empty");
            return false;
        } else if (!value.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();
        final Pattern PASSWORD_PATTERN = Pattern.compile("^" +

                "(?=.*[0-9])" +

                "(?=\\S+$)" +

                ".{4,}" +

                "$");

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(val).matches()) {
            password.setError("Should contain at least 4 characters!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

}