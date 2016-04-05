package com.onestepsearch.onestepsearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;

/**
 * Created by mdislam on 12/22/15.
 */
public class SignupSuccessfulActivity extends MainActivity {

    private String toEmail;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_successful_activity);

        TextView verificationMsg = (TextView) findViewById(R.id.toEmail);
        Intent intent = getIntent();

//        ParseUser.logOut();

        userId = intent.getStringExtra("userId");
        toEmail = intent.getStringExtra("toEmail");

        String email = getString(R.string.verification)+" "+toEmail;
        verificationMsg.setText(email);

        TextView loginBtn = (TextView) findViewById(R.id.loginBtn);
        TextView resendEmailBtn = (TextView) findViewById(R.id.resendEmailBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        resendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail(userId, toEmail);
            }
        });

    }




    private void login(){
        Intent intent = new Intent(SignupSuccessfulActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finish();
    }






}
