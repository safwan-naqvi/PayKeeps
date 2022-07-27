package com.example.paykeeperfyp.CommonCredential;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.paykeeperfyp.Admin.AdminDashboardActivity;
import com.example.paykeeperfyp.HelperClass.UserHelperClass;
import com.example.paykeeperfyp.IntroductoryScreen.introductoryActivity;
import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.example.paykeeperfyp.Receivers.InternetConnection;

import com.example.paykeeperfyp.User.UserDashboardActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    Button sign_up, login;
    CountryCodePicker countryCodePicker;
    TextInputLayout UserPhoneNumber, companiesRegistered, UserPassword;
    Switch simpleSwitch;
    RelativeLayout progressBar;
    CheckBox check_remember;
    AutoCompleteTextView companiesRegisteredNames;
    ArrayList<String> arrayList_Companies;
    public String db = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Paper.init(this); //Session Creation
        initWidget();
        checkIfNewCompanyOrNot();
        clickOnLogin();
        addCompanyToDropDown();


    }

    private void clickOnLogin() {

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_SMS,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkInternetConnection();
                            }
                        });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void initWidget() {
        arrayList_Companies = new ArrayList<>();
        sign_up = findViewById(R.id.create_new_account);
        login = findViewById(R.id.login_btn);
        UserPhoneNumber = (TextInputLayout) findViewById(R.id.et_user_phoneNo_login);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.country_code_pick_login);
        simpleSwitch = findViewById(R.id.toggle_admin);
        companiesRegistered = findViewById(R.id.companies_dropdown_login);
        companiesRegisteredNames = (AutoCompleteTextView) findViewById(R.id.companies_name_dropdown_login);
        UserPassword = (TextInputLayout) findViewById(R.id.et_user_password_login);
        progressBar = findViewById(R.id.progress_Dialog);
        check_remember = findViewById(R.id.check_remember);
    }

    private void firebaseLogin() {
        if (!validateFields() | !validatePhoneNumber() | !validateCompanyChoice()) {
            return;
        } else {
            progressBar.setVisibility(View.VISIBLE);
            //Get Data
            String _phoneNum = UserPhoneNumber.getEditText().getText().toString().trim();
            if (_phoneNum.charAt(0) == '0') {
                _phoneNum = _phoneNum.substring(1);
            }
            String _password = UserPassword.getEditText().getText().toString().trim();
            String _completeNumber = "+" + countryCodePicker.getSelectedCountryCode() + _phoneNum;
            String _chosenCompany = companiesRegisteredNames.getText().toString().trim();
            Log.i("checkdata", _completeNumber + "\n" +
                    _password + "\n" + _chosenCompany);

            //Query TO Check if user exists or not
            Query check;
            if (db.equals("Admin")) {
                check = FirebaseDatabase.getInstance().getReference("Company").child(_chosenCompany).child("Admin").orderByChild("phoneNo").equalTo(_completeNumber);
            } else {
                check = FirebaseDatabase.getInstance().getReference("Company").child(_chosenCompany).child("User").orderByChild("phoneNo").equalTo(_completeNumber);
            }
            check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserPhoneNumber.setError(null);
                        UserPhoneNumber.setErrorEnabled(false);
                        //Fetch User Data

                        UserHelperClass user = snapshot.child(_completeNumber).getValue(UserHelperClass.class);

                        //Fetching Password of User
                        String passFromDB = snapshot.child(_completeNumber).child("password").getValue(String.class);
                        if (passFromDB.equals(_password)) {
                            progressBar.setVisibility(View.GONE);
                            UserPassword.setError(null);
                            UserPassword.setErrorEnabled(false);
                            //Get User data from Firebase database

                            String _password = snapshot.child(_completeNumber).child("password").getValue(String.class);
                            String _phoneNo = snapshot.child(_completeNumber).child("phoneNo").getValue(String.class);
                            String _business = snapshot.child(_completeNumber).child("business").getValue(String.class);
                            //We will fetch some values of user that will be our future work for remember me task and
                            //Below would be the implementation of that using login session
                            //Creating a Login Session

                            if (check_remember.isChecked()) {
                                Paper.book().write(Prevalent.UserPhoneKey, _phoneNo);
                                Paper.book().write(Prevalent.UserPasswordKey, _password);
                                Paper.book().write(Prevalent.UserChosenBusiness, _business);
                                Paper.book().write(Prevalent.ParentDB, db);
                            }

                            SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
                            // Creating an Editor object to edit(write to the file)
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            // Storing the key and its value as the data fetched from edittext
                            myEdit.putString("number", _phoneNo);
                            // Once the changes have been made,
                            // we need to commit to apply those changes made,
                            // otherwise, it will throw an error
                            myEdit.commit();

                            Intent intent;
                            Prevalent.currentOnlineUser = user;
                            Prevalent.UserChosenBusiness = _chosenCompany;
                            if (db.equals("Admin")) {
                                Prevalent.ParentDB = "Admin";
                                intent = new Intent(getApplicationContext(), AdminDashboardActivity.class);
                            } else {
                                Prevalent.ParentDB = "User";
                                intent = new Intent(getApplicationContext(), UserDashboardActivity.class);
                            }
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Password is wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "User Does not exists", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Please check, if your registered with this company or not?", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void checkIfNewCompanyOrNot() {

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (simpleSwitch.isChecked()) {
                    db = "Admin";

                } else if (!simpleSwitch.isChecked()) {
                    db = "User";
                }
            }
        });

    }

    //Validations
    private boolean validateFields() {
        String _phoneNumber = UserPhoneNumber.getEditText().getText().toString().trim();
        String _password = UserPassword.getEditText().getText().toString().trim();

        if (_phoneNumber.isEmpty()) {
            UserPhoneNumber.setError("Phone Number cannot be empty");
            UserPhoneNumber.requestFocus();
            return false;
        } else if (_password.isEmpty()) {
            UserPassword.setError("Password cannot be Empty");
            UserPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        String val = UserPhoneNumber.getEditText().getText().toString().trim();
        // Creating a Pattern class object
        Pattern p = Pattern.compile("^\\d{10}$");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression for which
        // object of Matcher class is created
        Matcher m = p.matcher(val);
        if (val.isEmpty()) {
            UserPhoneNumber.setError("Enter valid phone number");
            return false;
        } else if (!m.matches()) {
            UserPhoneNumber.setError("No White spaces are allowed!");
            return false;
        } else {
            UserPhoneNumber.setError(null);
            UserPhoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCompanyChoice() {
        String val = companiesRegisteredNames.getText().toString().trim();
        if (val.isEmpty()) {
            companiesRegisteredNames.setError("Must Choose Any Company!");
            return false;
        } else {
            return true;
        }

    }

    public void checkInternetConnection() {

        InternetConnection internetConnection = new InternetConnection();

        if (!internetConnection.isConnected(this)) {
            showCustomDialog();
        } else {
            if (simpleSwitch.isChecked()) {
                db = "Admin";
            } else {
                db = "User";
            }
            firebaseLogin();
        }
    }

    //Custom Dialog
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                        startActivity(new Intent(LoginActivity.this, introductoryActivity.class));
                        finish();
                    }
                }).show();
    }

    private void addCompanyToDropDown() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Company");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayList_Companies.add(item.getKey().toString());
                }

                ArrayAdapter<String> arrayAdapter_Companies = new ArrayAdapter<>(LoginActivity.this, R.layout.dropdown_item, arrayList_Companies);
                companiesRegisteredNames.setAdapter(arrayAdapter_Companies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}