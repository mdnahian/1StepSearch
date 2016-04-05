package com.onestepsearch.onestepsearch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.core.InputFilter;


/**
 * Created by mdislam on 12/22/15.
 */
public class SignupActivity extends MainActivity {

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

        final TextView signupBtn = (TextView) findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signupBtn.setEnabled(false);

                String errorMsg = "";

                if(InputFilter.checkNumCharacters(fnameField.getText().toString(), 0)
                        && InputFilter.checkNumCharacters(lnameField.getText().toString(), 0)){
                    if(InputFilter.checkNumCharacters(userField.getText().toString(), 6)) {
                        if(InputFilter.checkNumCharacters(emailField.getText().toString(), 6)){
                            if(InputFilter.checkNumCharacters(passField.getText().toString(), 6)){
                                if(InputFilter.checkPassMatch(passField.getText().toString(), confirmField.getText().toString())){
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
                            .setIcon(R.drawable.logo_red)
                            .show();

                    signupBtn.setEnabled(true);
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


//        final ParseUser user = new ParseUser();
//        user.setUsername(username);
//        user.setPassword(password);
//        user.setEmail(email);
//        user.put("fname", fname);
//        user.put("lname", lname);
//        user.put("isNotificationsOn", true);
//        user.put("emailVerified", false);
//        user.put("numOfSearches", 100);
//        user.put("plan", "free");
//
//
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    sendVerificationEmail(user.getObjectId(), user.getEmail());
//                } else {
//                    new AlertDialog.Builder(SignupActivity.this)
//                            .setTitle("Sign Up Failed")
//                            .setMessage("Username is already in use. Please try again.")
//                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            })
//                            .setIcon(R.drawable.logo_red)
//                            .show();
//
//                    Log.d("Crash", e.getMessage());
//                }
//            }
//        });
    }



    private void login(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        this.startActivity(intent);
    }




}
