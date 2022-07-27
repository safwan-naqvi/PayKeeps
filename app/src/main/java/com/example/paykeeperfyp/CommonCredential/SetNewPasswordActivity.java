package com.example.paykeeperfyp.CommonCredential;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.Receivers.InternetConnection;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SetNewPasswordActivity extends AppCompatActivity {

    TextInputLayout pass, confirmPass;
    RelativeLayout progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_set_new_password);
        initWidget();

    }

    private void initWidget() {
        pass = findViewById(R.id.et_new_user_password);
        confirmPass = findViewById(R.id.et_new_user_confirm_password);
        progressDialog = findViewById(R.id.progress_bar4);
    }

    public void setNewPasswordBtn(View view) {

        InternetConnection checkInternet = new InternetConnection();
        if (!checkInternet.isConnected(this)) {
            showCustomDialog();
        }
        //validate Password
        if (!validatePassword() | !validateConfirmPassword()) {
            return;
        } else {
            progressDialog.setVisibility(View.VISIBLE);
            //New password data
            String _newPassword = pass.getEditText().getText().toString().trim();
            String _phoneNumber = getIntent().getStringExtra("phoneNo");

            //Update data in firebase and in sessions
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(_phoneNumber).child("password").setValue(_newPassword);
            // Toast.makeText(SetNewPassword.this, _newPassword, Toast.LENGTH_SHORT).show();
            progressDialog.setVisibility(View.GONE);
            startActivity(new Intent(getApplicationContext(), ForgetPasswordSuccessActivity.class));
            finish();
        }
    }

    private boolean validateConfirmPassword() {
        String password = pass.getEditText().getText().toString().trim();
        String cpass = confirmPass.getEditText().getText().toString().trim();
        if (cpass.isEmpty()) {
            confirmPass.setError("Field can not be empty");
            return false;
        } else if (!password.equals(cpass)) {
            pass.setError("Must Enter Same Password as above!");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        String val = pass.getEditText().getText().toString().trim();
        final Pattern PASSWORD_PATTERN = Pattern.compile("^" +

                "(?=.*[0-9])" +

                "(?=\\S+$)" +

                ".{4,}" +

                "$");

        if (val.isEmpty()) {
            pass.setError("Field can not be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(val).matches()) {
            pass.setError("Should contain at least 4 characters!");
            return false;
        } else {
            pass.setError(null);
            pass.setErrorEnabled(false);
            return true;
        }
    }

    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SetNewPasswordActivity.this);
        builder.setMessage("Please connect to the internet to proceed")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getApplicationContext(), introductoryActivity.class));
                        finish();
                    }
                }).show();
    }
}