package com.onestepsearch.onestepsearch.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.core.CrudInBackground;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.OnTaskCompleted;
import com.onestepsearch.onestepsearch.core.ParseResponse;


/**
 * Created by mdislam on 12/22/15.
 */
public class SignupActivity extends MainActivity {

    private TextView signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        final EditText fnameField = (EditText) findViewById(R.id.fnameField);
        final EditText lnameField = (EditText) findViewById(R.id.lnameField);
        final EditText userField = (EditText) findViewById(R.id.userField);
        final EditText emailField = (EditText) findViewById(R.id.emailField);
        final EditText phoneField = (EditText) findViewById(R.id.phoneField);
        final EditText passField = (EditText) findViewById(R.id.passField);
        final EditText confirmField = (EditText) findViewById(R.id.confirmField);

        signupBtn = (TextView) findViewById(R.id.signupBtn);
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
                                            emailField.getText().toString(), phoneField.getText().toString(), passField.getText().toString());

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


    private void signup(String fname, String lname, final String username, final String email, String phone, String password){


        final String verificationKey = generateRandomString();

        CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
            @Override
            public void onTaskComplete(String response) {
                ParseResponse parseResponse = new ParseResponse(response);
                parseResponse.execute();

                if(parseResponse.isError()){

                    signupBtn.setEnabled(true);

                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("Sign Up Failed")
                            .setMessage("Account already exists. Please try retrieving your password or create a new account.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.logo)
                            .show();

                } else {
                    sendVerificationEmail(username, email, verificationKey);
                }

            }
        });

        String sql = "INSERT INTO users (first_name, last_name, username, email, phone, password, emailVerification)" +
                " VALUES ('"+fname+"', '"+lname+"', '"+username+"', '"+email+"', '"+phone+"', '"+password+"', '"+verificationKey+"')";

        crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));

    }



    private void login(){
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        this.startActivity(intent);
        finish();
    }


}
