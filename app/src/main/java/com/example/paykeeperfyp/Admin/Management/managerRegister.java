package com.example.paykeeperfyp.Admin.Management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.Management.Database.DatabaseHelper;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;

public class managerRegister extends AppCompatActivity {

    Intent intent;
    String phone_number, passcode, name, businessname, address;
    TextInputLayout et_address;
    ProgressBar progressBar;
    ImageView nextArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_register);

        //Linking Objects
        et_address = findViewById(R.id.et_business_address);
        progressBar = findViewById(R.id.progressBar);
        nextArrow = findViewById(R.id.redirectSetup);
        //End of hooking
        //Initials
        progressBar.setVisibility(View.GONE);
        //End of Initials

        phone_number = Prevalent.currentOnlineUser.getPhoneNo();
        passcode = Prevalent.currentOnlineUser.getPassword();
        name = Prevalent.currentOnlineUser.getFullName();
        address = et_address.getEditText().getText().toString();
        String DB_name = Prevalent.currentOnlineUser.getPhoneNo();

        DatabaseHelper myDB = new DatabaseHelper(managerRegister.this);

        if (name.matches("^[a-zA-Z]{1}[a-zA-Z ]*$")) {
            businessname = Prevalent.UserChosenBusiness;
        }
        Log.i("checkxx", businessname + "   " + Prevalent.currentOnlineUser.getFullName());

        nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                et_address.setVisibility(View.GONE);
                // && !address.isEmpty()
                if (name != null && !businessname.isEmpty() && !et_address.getEditText().getText().toString().isEmpty()) {
                    if (!name.matches("^[a-zA-Z]{1}[a-zA-Z ]*$") && !businessname.matches("^[a-zA-Z]{1}[a-zA-Z ]*$") && !et_address.getEditText().getText().toString().matches("^[a-zA-Z]{1}[a-zA-Z ]*$")){
                        Toast.makeText(getApplicationContext(), "Name Match Issue", Toast.LENGTH_SHORT).show();
                    } else {
                        Resources res = getResources();
                        String uri = "@drawable/" + name.substring(0, 1).toLowerCase();
                        int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                        Drawable drawable = res.getDrawable(imageResource);
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] image = stream.toByteArray();
                        DatabaseHelper myDB = new DatabaseHelper(managerRegister.this);
                        if (myDB.storeNewUserData(phone_number, name, passcode, businessname, et_address.getEditText().getText().toString(), image)) {
                            SharedPreferences SharedPreferences = getSharedPreferences(DB_name, MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = SharedPreferences.edit();
                            myEdit.putBoolean("first_time_login", false);
                            myEdit.putBoolean("is_logged_in", false);
                            myEdit.commit();
                            Toast.makeText(managerRegister.this, "Account Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(managerRegister.this, managerDashboard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(managerRegister.this, "Something Went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "XYz", Toast.LENGTH_SHORT).show();
                    if (address.isEmpty()) {
                        et_address.setError("Field cannot be empty");
                    } else if (!address.matches("^[a-zA-Z]{1}[a-zA-Z ]*$")) {
                        et_address.setError("Require Character and Whitespace Only");
                    } else {
                        et_address.setError(null);
                    }
                }
            }
        });


    }
}