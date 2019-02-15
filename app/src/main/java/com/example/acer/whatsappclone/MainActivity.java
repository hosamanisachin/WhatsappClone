package com.example.acer.whatsappclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private EditText mPhoneNumber, mCode;
    private Button mSend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks; //to handle everything

    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        //if the user is already logged in we can directly move to MainPage activity
        userIsLoggedIn();

        mPhoneNumber = findViewById(R.id.phonenumber);
        mCode = findViewById(R.id.code);

        mSend = findViewById(R.id.send); //button
/*
        //the object and method for callbacks
        mSend.setOnClickListener((v) -> {
            startPhoneNumberVerification();
        }
        });
        */

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            //on fail
            @Override
            public void onVerificationFailed(FirebaseException e) {}

            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(verificationId, forceResendingToken);

                mVerificationId = verificationId;
                mSend.setText("Verify Code");
            }
        };



        //button listener
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVerificationId != null){
                    verifyPhoneNumberWithCode(); //mVerificationId, mCode.getText().toString());
                }
                else
                startPhoneNumberVerification();

            }
        });

    }

    private void verifyPhoneNumberWithCode() {//String verificationId. String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,mCode.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                    //we sahll call a function
                    userIsLoggedIn(); //if user is different
            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
            return;
        }
    }


    private void startPhoneNumberVerification() {
        //firebase documnetation
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber.getText().toString(), //get the text
                60, //timout length
                TimeUnit.SECONDS, //timout unit
                MainActivity.this, //pass the acitivity
                mCallbacks); // a callback to handle what happens next -> failures or success
    }
}
