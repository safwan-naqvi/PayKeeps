package com.example.paykeeperfyp.CommonCredential;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.paykeeperfyp.R;

import java.util.Calendar;

public class RegisterActivityTwo extends AppCompatActivity {

    Button next;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_page_two);
        initWidget();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNextSignupScreen();
            }
        });

    }

    private void initWidget() {
        next = findViewById(R.id.signup_next_btn2);
        radioGroup = findViewById(R.id.radio_gender);
        datePicker = findViewById(R.id.age_picker);

    }

    public void gotoLoginActivity(View view) {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return false;
        } else {
            return true;
        }
    }
    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 14) {
            Toast.makeText(this, "You are not eligible to register", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    public void callNextSignupScreen() {

        if (!validateAge() | !validateGender()) {
            return;
        } else {
            //Previous Collected Data
            String name = getIntent().getStringExtra("name");
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");
            //------
            selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
            String _gender = selectedGender.getText().toString();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            String _date = day+"/"+month+"/"+year;

            Intent intent = new Intent(getApplicationContext(), RegisterActivityThree.class);
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            intent.putExtra("password",password);
            intent.putExtra("gender",_gender);
            intent.putExtra("age",_date);

            startActivity(intent);
        }
    }
}