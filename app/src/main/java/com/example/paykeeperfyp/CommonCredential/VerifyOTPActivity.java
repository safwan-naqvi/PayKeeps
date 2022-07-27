package com.example.paykeeperfyp.CommonCredential;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.paykeeperfyp.HelperClass.AdminHelperClass;
import com.example.paykeeperfyp.HelperClass.UserHelperClass;
import com.example.paykeeperfyp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {

    PinView pinFromUser;
    String codeBySystem;
    String _name, _email, _password, _date, _gender, _phoneNo, _whatToDo, _business, _choosenBusiness, _parentDB;
    RelativeLayout progressBar;
    TextView otp_Send_no;
    Button btn_verify_resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_otpactivity);

        initWidget();
        collectingDataFromActivity();
        Log.i("check", _name + "\n" + _email + "\n" + _password + "\n" + _date + "\n" + _gender + "\n" + _phoneNo + "\n" + _parentDB + "\n" + _business + "\n" + _choosenBusiness);
        //Setting the Number on textView
        otp_Send_no.setText(_phoneNo);

    }

    private void collectingDataFromActivity() {
        /*Collecting Data from previous activities
        _name = getIntent().getStringExtra("name");
        _email = getIntent().getStringExtra("email");
        _password = getIntent().getStringExtra("password");
        _date = getIntent().getStringExtra("age");
        _gender = getIntent().getStringExtra("gender");
        _phoneNo = getIntent().getStringExtra("phoneNo");
        _whatToDo = getIntent().getStringExtra("whatToDo");
        _parentDB = getIntent().getStringExtra("parentDB");
        _business = getIntent().getStringExtra("business");
        _choosenBusiness = getIntent().getStringExtra("choiceCompany");*/

        //Collecting Data from previous activities
        _name = getIntent().getStringExtra("name");
        _email = getIntent().getStringExtra("email");
        _password = getIntent().getStringExtra("password");
        _date = getIntent().getStringExtra("age");
        _gender = getIntent().getStringExtra("gender");
        _phoneNo = getIntent().getStringExtra("phoneNo");
        _whatToDo = getIntent().getStringExtra("whatToDo");
        _parentDB = getIntent().getStringExtra("parentDB");
        _business = getIntent().getStringExtra("business");
        _choosenBusiness = getIntent().getStringExtra("choiceCompany");


        sendVerificationCodeToUser(_phoneNo);
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        //For Manual entered phone number from another device
                        codeBySystem = s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //System entered code
                        String code = phoneAuthCredential.getSmsCode();
                        if (code != null) {
                            pinFromUser.setText(code);
                            verifyCode(code);
                        }
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        progressBar.setVisibility(View.VISIBLE);
        btn_verify_resend.setVisibility(View.GONE);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Verification is Completed now either
                            //We will store data or update that user password
                            if (_whatToDo.equals("updateData")) {
                                updateOldUsersData();
                            } else if (_whatToDo.equals("createNewUser")) {
                                storeNewUsersData();
                            }

                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTPActivity.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void storeNewUsersData() {
        FirebaseDatabase rootRef = FirebaseDatabase.getInstance(); //Database location
        DatabaseReference reference = rootRef.getReference("Company"); //Table of User
        if (!_business.equals("") || _choosenBusiness.equals("")) {
            AdminHelperClass helper = new AdminHelperClass(_name, _email, _password, _phoneNo, _date, _gender, _business);
            reference.child(_business).child(_parentDB).child(_phoneNo).setValue(helper);
            Log.i("checkData", "I am business");
        } else {
            UserHelperClass helper = new UserHelperClass(_name, _email, _password, _phoneNo, _date, _gender, _choosenBusiness);
            reference.child(_choosenBusiness).child(_parentDB).child(_phoneNo).setValue(helper);
            Log.i("checkData", "I am not business");
        }

        Intent intent = new Intent(getApplicationContext(), RegisteredSuccessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateOldUsersData() {
        Intent intent = new Intent(VerifyOTPActivity.this, SetNewPasswordActivity.class);
        intent.putExtra("phoneNo", _phoneNo);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void initWidget() {
        //hooks
        otp_Send_no = findViewById(R.id.verify_otp_number);
        progressBar = findViewById(R.id.progress_bar5);
        pinFromUser = findViewById(R.id.pin_view);
        btn_verify_resend = findViewById(R.id.btn_verify_resend);
    }

    public void callNextScreenFromOTP(View view) {
        String code = pinFromUser.getText().toString();
        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_SHORT).show();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }


}