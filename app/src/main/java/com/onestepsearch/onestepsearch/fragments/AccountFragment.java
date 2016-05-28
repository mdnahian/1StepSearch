package com.onestepsearch.onestepsearch.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.activities.ParentActivity;
import com.onestepsearch.onestepsearch.core.CrudInBackground;
import com.onestepsearch.onestepsearch.core.InputFilter;
import com.onestepsearch.onestepsearch.core.OnTaskCompleted;
import com.onestepsearch.onestepsearch.core.ParseResponse;
import com.onestepsearch.onestepsearch.core.SavedSession;


/**
 * Created by mdislam on 12/26/15.
 */
public class AccountFragment extends Fragment {


    private ParentActivity parentActivity;
    private SavedSession savedSession;

    private TextView saveBtn;
    private TextView deleteBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_activity, container, false);

        parentActivity = (ParentActivity) getActivity();

        savedSession = (SavedSession) getActivity().getIntent().getSerializableExtra("SavedSession");
        if(savedSession.getUsername().equals("")){
            parentActivity.logout();
        }

        final EditText fnameField = (EditText) rootView.findViewById(R.id.fnameField);
        final EditText lnameField = (EditText) rootView.findViewById(R.id.lnameField);
        final EditText userField = (EditText) rootView.findViewById(R.id.userField);
        final EditText emailField = (EditText) rootView.findViewById(R.id.emailField);
        final EditText phoneField = (EditText) rootView.findViewById(R.id.phoneField);
        final EditText passField = (EditText) rootView.findViewById(R.id.passField);
        final EditText confirmField = (EditText) rootView.findViewById(R.id.confirmField);

        fnameField.setText(savedSession.getFname());
        lnameField.setText(savedSession.getLname());
        userField.setText(savedSession.getUsername());
        emailField.setText(savedSession.getEmail());
        phoneField.setText(savedSession.getPhone());


        deleteBtn = (TextView) rootView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(passField.getText().toString().equals(confirmField.getText().toString())){
                    deleteAccount();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Failed to Delete Account")
                            .setMessage("Please enter your password and try again.")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.logo)
                            .show();
                }


            }
        });


        saveBtn = (TextView) rootView.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveBtn.setEnabled(false);

                String errorMsg = "";

                if(InputFilter.checkNumCharacters(fnameField.getText().toString(), 0)
                        && InputFilter.checkNumCharacters(lnameField.getText().toString(), 0)){
                    if(InputFilter.checkNumCharacters(userField.getText().toString(), 6)) {
                        if(InputFilter.checkNumCharacters(emailField.getText().toString(), 6)){
                            if(InputFilter.checkNumCharacters(passField.getText().toString(), 6)){
                                if(InputFilter.checkPassMatch(passField.getText().toString(), confirmField.getText().toString())){
                                    saveChanges(fnameField.getText().toString(), lnameField.getText().toString(), userField.getText().toString(),
                                            emailField.getText().toString(), phoneField.getText().toString(), passField.getText().toString());

                                    passField.setText("");
                                    confirmField.setText("");
                                } else{
                                    errorMsg += "Passwords do not match. Please try again.";
                                }
                            } else{
                                errorMsg += "Please fill password to save changes.";
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
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Failed to Save Changes")
                            .setMessage(errorMsg)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.logo)
                            .show();

                    saveBtn.setEnabled(true);
                }

            }
        });

        return rootView;
    }



    private void saveChanges(String fname, String lname, final String username, final String email, String phone, String password){

        CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
            @Override
            public void onTaskComplete(String response) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Changes Saved Successfully")
                        .setMessage("Please log back in for changes to take effect.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                parentActivity.logout();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                parentActivity.logout();
                            }
                        })
                        .setIcon(R.drawable.logo)
                        .show();
            }
        });

        String sql = "UPDATE users SET first_name='"+fname+"', last_name='"+lname+"', username='"+username+"', phone='"+phone+"', password='"+password+"' WHERE email='"+email+"'";

        crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));
    }



    private void deleteAccount(){

        new AlertDialog.Builder(getActivity())
                .setTitle("Are you sure you want to delete your account?")
                .setMessage("This action is irreversible and will make your account unusable.")
                .setPositiveButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
                            @Override
                            public void onTaskComplete(String response) {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Your account has been deleted.")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                parentActivity.logout();
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                parentActivity.logout();
                                            }
                                        })
                                        .setIcon(R.drawable.logo)
                                        .show();
                            }
                        });

                        String sql = "DELETE FROM users WHERE username='"+savedSession.getUsername()+"' AND email='"+savedSession.getEmail()+"'";

                        crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));

                    }
                })
                .setIcon(R.drawable.logo)
                .show();




    }



}
