package com.example.paykeeperfyp.IntroductoryScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.AdminDashboardActivity;
import com.example.paykeeperfyp.CommonCredential.LoginActivity;
import com.example.paykeeperfyp.CommonCredential.RegisterActivity;
import com.example.paykeeperfyp.HelperClass.UserHelperClass;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.Receivers.InternetConnection;
import com.example.paykeeperfyp.User.UserDashboardActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class introductoryActivity extends AppCompatActivity {
    Button signin, signup;
    RelativeLayout progressBar;
    TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_introductory);

        Paper.init(this);

        initWidget();

        String userPhone = Paper.book().read(Prevalent.UserPhoneKey);
        String userPass = Paper.book().read(Prevalent.UserPasswordKey);
        String parentDB = Paper.book().read(Prevalent.ParentDB);
        String companyDB = Paper.book().read(Prevalent.UserChosenBusiness);

        Log.i("checkValue", userPhone + " " + userPass + " " + parentDB + " " + companyDB);

        if (!checkInternetConnectivity()) {
            showInternetConnectivityCustomDialog();
        } else {
            if (!TextUtils.isEmpty(userPass) && !TextUtils.isEmpty(userPhone) && !TextUtils.isEmpty(parentDB) && !TextUtils.isEmpty(companyDB)) {
                checkPrevalentStorage(userPhone, userPass, parentDB, companyDB);
            }
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkInternetConnectivity()) {
                    showInternetConnectivityCustomDialog();
                } else {
                    callLoginScreen();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkInternetConnectivity()) {
                    showInternetConnectivityCustomDialog();
                } else {
                    callRegisterScreen();
                }
            }
        });

    }

    private void checkPrevalentStorage(String userPhone, String userPass, String parentDB, String company) {
        setActivityHide();
        progressBar.setVisibility(View.VISIBLE);
        AllowAccess(userPhone, userPass, parentDB, company);
    }

    private void AllowAccess(final String userPhone, final String userPass, final String PARENT_DB, final String company) {
        //Query TO Check if user exists or not
        Query check;
        if (PARENT_DB.equals("Admin")) {
            check = FirebaseDatabase.getInstance().getReference("Company").child(company).child("Admin").orderByChild("phoneNo").equalTo(userPhone);
        } else {
            check = FirebaseDatabase.getInstance().getReference("Company").child(company).child("User").orderByChild("phoneNo").equalTo(userPhone);
        }
        check.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    UserHelperClass user = snapshot.child(userPhone).getValue(UserHelperClass.class);

                    String passFromDB = snapshot.child(userPhone).child("password").getValue(String.class);

                    if (passFromDB.equals(userPass)) {
                        progressBar.setVisibility(View.GONE);

                        //Get User data from Firebase database

                        String _fullName = snapshot.child(userPhone).child("fullName").getValue(String.class);
                        String _username = snapshot.child(userPhone).child("username").getValue(String.class);
                        String _email = snapshot.child(userPhone).child("email").getValue(String.class);
                        String _password = snapshot.child(userPhone).child("password").getValue(String.class);
                        String _phoneNo = snapshot.child(userPhone).child("phoneNo").getValue(String.class);
                        String _dateOfBirth = snapshot.child(userPhone).child("age").getValue(String.class);
                        String _gender = snapshot.child(userPhone).child("gender").getValue(String.class);

                        //Creating a Login Session

                        Prevalent.currentOnlineUser = user;
                        if (PARENT_DB.equals("Admin")) {
                            startActivity(new Intent(getApplicationContext(), AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), UserDashboardActivity.class));
                        }
                        finish();

                    } else {
                        progressBar.setVisibility(View.GONE);
                        setActivityShow();
                        Toast.makeText(getApplicationContext(), "Password is wrong", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    setActivityShow();
                    Toast.makeText(getApplicationContext(), "User Does not exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                setActivityShow();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setActivityHide() {
        signup.setVisibility(View.INVISIBLE);
        signin.setVisibility(View.INVISIBLE);
        desc.setVisibility(View.GONE);
    }

    public void setActivityShow() {
        signup.setVisibility(View.VISIBLE);
        signin.setVisibility(View.VISIBLE);
        desc.setVisibility(View.VISIBLE);
    }

    private void callRegisterScreen() {
        startActivity(new Intent(introductoryActivity.this, RegisterActivity.class));
    }

    private void callLoginScreen() {
        startActivity(new Intent(introductoryActivity.this, LoginActivity.class));
    }

    private void initWidget() {
        //--Hook------------------------------
        signin = findViewById(R.id.signin_btn);
        signup = findViewById(R.id.signup_btn);
        progressBar = findViewById(R.id.progress_bar);
        desc = findViewById(R.id.welcome_desc);
        //-------------------------------------
    }

    public boolean checkInternetConnectivity() {

        InternetConnection internetConnection = new InternetConnection();

        if (!internetConnection.isConnected(this)) {
            return false;
        } else {
            return true;
        }
    }

    private void showInternetConnectivityCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(introductoryActivity.this);
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