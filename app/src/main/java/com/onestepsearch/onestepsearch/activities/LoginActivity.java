package com.onestepsearch.onestepsearch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.core.InputFilter;


/**
 * Created by mdislam on 12/21/15.
 */
public class LoginActivity extends MainActivity {


    private boolean isSessionSaved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

//        try {
//            Parse.enableLocalDatastore(this);
//            Parse.initialize(new Parse.Configuration.Builder(this)
//                    .applicationId(getString(R.string.parseAppId))
//                    .clientKey(null)
//                    .server(getString(R.string.parseURL)).build()
//            );
//            ParseInstallation.getCurrentInstallation().saveInBackground();
//        } catch (Exception e){
//            Log.d("Crash", "Application Crashed. Error Report Sent");
//        }
//
//        final ParseUser currentUser = ParseUser.getCurrentUser();
//        if (currentUser != null) {
//            if(currentUser.getBoolean("emailVerified")){
//                onPostLogin();
//            } else{
//                ParseUser.logOut();
//            }
//        }


        TextView logoText = (TextView) findViewById(R.id.logo);
        logoText.requestFocus();

        final EditText userField = (EditText) findViewById(R.id.userField);
        final EditText passField = (EditText) findViewById(R.id.passField);
        final TextView loginBtn = (TextView) findViewById(R.id.loginBtn);
        final TextView forgotBtn = (TextView) findViewById(R.id.forgotBtn);
        final TextView signupBtn = (TextView) findViewById(R.id.signupBtn);
        Switch rememberSwitch = (Switch) findViewById(R.id.rememberSwitch);


        isSessionSaved = true;
        rememberSwitch.setChecked(true);
        rememberSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSessionSaved) {
                    isSessionSaved = false;
                } else {
                    isSessionSaved = true;
                }
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userField.getText().toString();
                String password = passField.getText().toString();

                if(InputFilter.checkNumCharacters(username, 0) && InputFilter.checkNumCharacters(password, 0)){
                    login(username, password);
                } else{
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Try Again")
                            .setMessage("Please Provide a Valid Username and Password")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.logo_red)
                            .show();
                }


            }
        });


        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });


    }




    private void login(String username, String password){
//        ParseUser.logInInBackground(username, password, new LogInCallback() {
//            public void done(final ParseUser user, ParseException e) {
//                if (user != null) {
//                    if (user.getBoolean("emailVerified")) {
//                        onPostLogin();
//                    } else {
//                        new AlertDialog.Builder(LoginActivity.this)
//                                .setTitle("Confirm Email Address")
//                                .setMessage("You have not yet confirmed your email address "+user.getEmail()+".")
//                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                })
//                                .setNegativeButton("Resend Email", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        sendVerificationEmail(user.getObjectId(), user.getEmail());
//                                    }
//                                })
//                                .setIcon(R.drawable.logo_red)
//                                .show();
//                    }
//                } else {
//                    new AlertDialog.Builder(LoginActivity.this)
//                            .setTitle("Try Again")
//                            .setMessage("Username or Password Incorrect")
//                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                }
//                            })
//                            .setIcon(R.drawable.logo_red)
//                            .show();
//                }
//            }
//        });
    }




    private void onPostLogin(){
        Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
        this.startActivity(intent);
        finish();
    }



    private void forgot(){
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        this.startActivity(intent);
    }


    private void signup(){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        this.startActivity(intent);
    }





}