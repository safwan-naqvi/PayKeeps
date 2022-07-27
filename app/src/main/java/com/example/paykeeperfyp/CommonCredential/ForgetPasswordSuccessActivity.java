package com.example.paykeeperfyp.CommonCredential;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.paykeeperfyp.R;

public class ForgetPasswordSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_success);
    }

    public void callLoginScreenFromVerification(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}