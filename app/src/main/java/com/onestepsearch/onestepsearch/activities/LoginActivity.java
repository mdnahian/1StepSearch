package com.onestepsearch.onestepsearch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.core.CrudInBackground;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.OnTaskCompleted;
import com.onestepsearch.onestepsearch.core.ParseResponse;
import com.onestepsearch.onestepsearch.core.SavedSession;

import java.util.ArrayList;


/**
 * Created by mdislam on 12/21/15.
 */
public class LoginActivity extends MainActivity {


    private boolean isSessionSaved;
    private SavedSession savedSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        savedSession = getSavedSession();
        if(savedSession.getUsername() != null){
            onPostLogin();
        }

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
                            .setMessage("Please provide a valid username and password.")
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

        CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
            @Override
            public void onTaskComplete(String response) {

                Log.d("Crash", response);

                ParseResponse parseResponse = new ParseResponse(response);
                parseResponse.execute();

                if(parseResponse.isError()){
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Try Again")
                            .setMessage("Username or Password Incorrect")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.logo_red)
                            .show();
                } else {
                    ArrayList objects = parseResponse.getObjects();

                    if (objects != null) {
                        savedSession = new SavedSession();

                        ArrayList object = (ArrayList) objects.get(0);

                        savedSession.setFname(object.get(1).toString());
                        savedSession.setLname(object.get(2).toString());
                        savedSession.setUsername(object.get(3).toString());
                        savedSession.setEmail(object.get(4).toString());
                        savedSession.setPhone(object.get(5).toString());
                        savedSession.setEmailVerification(object.get(7).toString());
                        savedSession.setNumOfSearches(Integer.parseInt(String.valueOf(object.get(8))));
                        savedSession.setCurrentNumOfSearches(Integer.parseInt(String.valueOf(object.get(9))));
                        savedSession.setPlan(object.get(10).toString());
                        savedSession.setPlanExpiration(object.get(11).toString());

                        if(savedSession.getUsername() != null && savedSession.getEmailVerification().equals("true")){
                            createSavedSession(savedSession);
                            onPostLogin();
                        } else {
                            new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Confirm Email Address")
                                .setMessage("You have not yet confirmed your email address "+savedSession.getEmail()+".")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton("Resend Email", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendVerificationEmail(savedSession.getUsername(), savedSession.getEmail(), "");
                                    }
                                })
                                .setIcon(R.drawable.logo_red)
                                .show();
                        }


                    }

                }


            }
        });

        String sql = "SELECT * FROM users WHERE username='"+username+"' AND password='"+password+"'";

        crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));


    }




    private void onPostLogin(){
        Intent intent = new Intent(LoginActivity.this, ParentActivity.class);
        intent.putExtra("SavedSession", savedSession);
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
