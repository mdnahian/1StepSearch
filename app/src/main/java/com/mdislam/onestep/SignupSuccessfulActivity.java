package com.mdislam.onestep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by mdislam on 12/22/15.
 */
public class SignupSuccessfulActivity extends Activity {

    private String toEmail;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_successful_activity);

        TextView verificationMsg = (TextView) findViewById(R.id.toEmail);
        Intent intent = getIntent();

        ParseUser.logOut();

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
                resendEmail();
                Toast.makeText(SignupSuccessfulActivity.this, "Email Resent", Toast.LENGTH_LONG).show();
            }
        });

    }



    private void resendEmail(){

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                    ParseUser user = objects.get(0);
                    user.put("email", user.getEmail());
                    user.saveInBackground();

                    ParseUser.logOut();
                } else {
                    Log.d("Crash", "Could not send email.");
                }
            }
        });

    }



    private void login(){
        Intent intent = new Intent(SignupSuccessfulActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finish();
    }






}
