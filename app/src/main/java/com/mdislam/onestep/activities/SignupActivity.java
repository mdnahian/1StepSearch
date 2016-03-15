package com.mdislam.onestep.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mdislam.onestep.R;
import com.mdislam.onestep.core.InputFilter;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by mdislam on 12/22/15.
 */
public class SignupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        final EditText fnameField = (EditText) findViewById(R.id.fnameField);
        final EditText lnameField = (EditText) findViewById(R.id.lnameField);
        final EditText userField = (EditText) findViewById(R.id.userField);
        final EditText emailField = (EditText) findViewById(R.id.emailField);
        final EditText passField = (EditText) findViewById(R.id.passField);
        final EditText confirmField = (EditText) findViewById(R.id.confirmField);

        TextView signupBtn = (TextView) findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String errorMsg = "";

                InputFilter inputFilter = new InputFilter();
                if(inputFilter.checkNumCharacters(fnameField.getText().toString(), 0)
                        && inputFilter.checkNumCharacters(lnameField.getText().toString(), 0)){
                    if(inputFilter.checkNumCharacters(userField.getText().toString(), 6)) {
                        if(inputFilter.checkNumCharacters(emailField.getText().toString(), 6)){
                            if(inputFilter.checkNumCharacters(passField.getText().toString(), 6)){
                                if(inputFilter.checkPassMatch(passField.getText().toString(), confirmField.getText().toString())){
                                    signup(fnameField.getText().toString(), lnameField.getText().toString(), userField.getText().toString(),
                                            emailField.getText().toString(), passField.getText().toString());

                                    fnameField.setText("");
                                    lnameField.setText("");
                                    emailField.setText("");
                                    userField.setText("");
                                    passField.setText("");
                                    confirmField.setText("");
                                } else{
                                    errorMsg += "Passwords do not match. Please try again.";
                                }
                            } else{
                                errorMsg += "Password must be at least 6 characters long.";
                            }
                        } else{
                            errorMsg += "Please provide a valid email address.";
                        }
                    } else {
                        errorMsg += "Username must be at least 6 characters long.";
                    }
                } else {
                    errorMsg += "Please provide a valid full name.";
                }


                if(errorMsg.equals("")){

                } else{
                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("Sign Up Failed")
                            .setMessage(errorMsg)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.alert)
                            .show();
                }


            }
        });


        TextView loginBtn = (TextView) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });



    }


    private void signup(String fname, String lname, String username, final String email, String password){


        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.put("fname", fname);
        user.put("lname", lname);
        user.put("isNotificationsOn", true);
        user.put("emailVerified", false);
        user.put("numOfSearches", 100);


        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    sendVerificationEmail(user.getObjectId(), user.getEmail());
                } else {
                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("Sign Up Failed")
                            .setMessage("Username is already in use. Please try again.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.alert)
                            .show();

                    Log.d("Crash", e.getMessage());
                }
            }
        });
    }


    private void sendVerificationEmail(String userId, String toEmail){
        Intent intent = new Intent(SignupActivity.this, SignupSuccessfulActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("toEmail", toEmail);
        this.startActivity(intent);
    }


    private void login(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        this.startActivity(intent);
    }




}
