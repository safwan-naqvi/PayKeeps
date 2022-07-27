package com.example.paykeeperfyp.CommonCredential;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.paykeeperfyp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivityThree extends AppCompatActivity {

    Button verify;
    CountryCodePicker countryCodePicker;
    TextInputLayout UserPhoneNumber, newCompanyName, companiesRegistered;
    AutoCompleteTextView companiesRegisteredNames;
    String _getUserEnteredPhoneNumber;
    Switch simpleSwitch;
    String db = "User";

    ArrayList<String> arrayList_Companies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_three);
        arrayList_Companies = new ArrayList<>();
        initWidget();


        checkIfNewCompanyOrNot();
        addCompanyToDropDown();


    }

    private void addCompanyToDropDown() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Company");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot item : snapshot.getChildren()) {
                    arrayList_Companies.add(item.getKey().toString());
                }

                ArrayAdapter<String> arrayAdapter_Companies = new ArrayAdapter<>(RegisterActivityThree.this, R.layout.dropdown_item, arrayList_Companies);
                companiesRegisteredNames.setAdapter(arrayAdapter_Companies);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkIfNewCompanyOrNot() {

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (simpleSwitch.isChecked()) {
                    db = "Admin";
                    newCompanyName.setVisibility(View.VISIBLE);
                    companiesRegistered.setVisibility(View.GONE);
                } else {
                    db = "User";
                    newCompanyName.setVisibility(View.GONE);
                    companiesRegistered.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void initWidget() {
        verify = findViewById(R.id.signup_next_btn3);
        UserPhoneNumber = (TextInputLayout) findViewById(R.id.et_user_phoneNo_reg);
        countryCodePicker = (CountryCodePicker) findViewById(R.id.country_code_pick);
        simpleSwitch = findViewById(R.id.toggle_admin_reg);
        companiesRegistered = findViewById(R.id.companies_dropdown);
        newCompanyName = (TextInputLayout) findViewById(R.id.et_new_company_name);
        companiesRegisteredNames = (AutoCompleteTextView) findViewById(R.id.companies_name_dropdown);
    }

    public void gotoLoginActivity(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
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

    public void gotoVerifyOTP() {
        if (simpleSwitch.isChecked()) {
            if (!validatePhoneNumber() || !validateBusinessName()) {
                return;
            } else {
                //Previous Collected Data
                String _name = getIntent().getStringExtra("name");
                String _email = getIntent().getStringExtra("email");
                String _password = getIntent().getStringExtra("password");
                String _date = getIntent().getStringExtra("age");
                String _gender = getIntent().getStringExtra("gender");
                //------
                //Get complete phone number
                _getUserEnteredPhoneNumber = UserPhoneNumber.getEditText().getText().toString();
                //Remove first zero if entered!
                if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
                    _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
                }
                String _phoneNo = "+" + countryCodePicker.getSelectedCountryCode() + _getUserEnteredPhoneNumber;
                String _business = newCompanyName.getEditText().getText().toString();
                String _chosenRegisteredCompany = companiesRegisteredNames.getText().toString();

                //Sending All Data to Verification Activity
                Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                intent.putExtra("name", _name);
                intent.putExtra("email", _email);
                intent.putExtra("password", _password);
                intent.putExtra("age", _date);
                intent.putExtra("gender", _gender);
                intent.putExtra("phoneNo", _phoneNo);
                intent.putExtra("business", _business);
                intent.putExtra("choiceCompany", _chosenRegisteredCompany);

                intent.putExtra("whatToDo", "createNewUser");
                intent.putExtra("parentDB", db);
                Log.i("checkData", "Name: " + _name + " Email: " + _email + " password: " + _password + " Date: " + _date + " Gender: " + _gender + " Phone: "
                        + _phoneNo + " Business: " + _business + "  choosed Company " + _chosenRegisteredCompany);

                startActivity(intent);
                finish();


            }
        } else {
            if (!validatePhoneNumber() || !validateCompanyChoice()) {
                return;
            } else {
                //Previous Collected Data
                String _name = getIntent().getStringExtra("name");
                String _email = getIntent().getStringExtra("email");
                String _password = getIntent().getStringExtra("password");
                String _date = getIntent().getStringExtra("age");
                String _gender = getIntent().getStringExtra("gender");
                //------
                //Get complete phone number
                _getUserEnteredPhoneNumber = UserPhoneNumber.getEditText().getText().toString();
                //Remove first zero if entered!
                if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
                    _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
                }
                String _phoneNo = "+" + countryCodePicker.getSelectedCountryCode() + _getUserEnteredPhoneNumber;
                String _business = newCompanyName.getEditText().getText().toString();
                String _chosenRegisteredCompany = companiesRegisteredNames.getText().toString();

                //Sending All Data to Verification Activity
                Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                intent.putExtra("name", _name);
                intent.putExtra("email", _email);
                intent.putExtra("password", _password);
                intent.putExtra("age", _date);
                intent.putExtra("gender", _gender);
                intent.putExtra("phoneNo", _phoneNo);
                intent.putExtra("business", _business);
                intent.putExtra("choiceCompany", _chosenRegisteredCompany);

                intent.putExtra("whatToDo", "createNewUser");
                intent.putExtra("parentDB", db);
                Log.i("checkData", "Name: " + _name + " Email: " + _email + " password: " + _password + " Date: " + _date + " Gender: " + _gender + " Phone: "
                        + _phoneNo + " Business: " + _business + "  choosed Company " + _chosenRegisteredCompany);

                startActivity(intent);
                finish();


            }
        }

    }

    private boolean validateBusinessName() {

        String val = newCompanyName.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            newCompanyName.setError("Company Name is Required!");
            return false;
        } else {
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

    public void finishSignUp(View view) {
        Toast.makeText(this, "fun", Toast.LENGTH_SHORT).show();
        gotoVerifyOTP();
    }
}