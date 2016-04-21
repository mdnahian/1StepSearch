package com.onestepsearch.onestepsearch.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.onestepsearch.onestepsearch.R;
import com.onestepsearch.onestepsearch.core.CrudInBackground;
import com.onestepsearch.onestepsearch.core.OnTaskCompleted;
import com.onestepsearch.onestepsearch.core.SavedSession;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by mdislam on 3/23/16.
 */
public class MainActivity extends Activity {

    private static final String EMAIL_URL = "http://1stepsearch.com/api/send.php";


    public void sendVerificationEmail(final String username, final String email, String key){

        if(key.equals("")){
            final String verificationKey = generateRandomString();

            CrudInBackground crudInBackground = new CrudInBackground(new OnTaskCompleted() {
                @Override
                public void onTaskComplete(String response) {
                    SendEmail sendEmail = new SendEmail(username);
                    sendEmail.execute(email, "do_not_reply@1stepsearch.com", "1Step Search - Verify Your Email Address",
                            "Hello " + username + ",<br><br>" +
                                    "To verify your email address " + email + ", please go to the following link:<br>" +
                                    "<a href='http://1stepsearch.com/verification/" + verificationKey + "'>http://1stepsearch.com/verification/" + verificationKey + "</a>" +
                                    "<br><br>" +
                                    "<small>This email was automatically generated. If you did not register for an account at 1Step Search, please disregard this email.</small>");
                }
            });

            String sql = "UPDATE users SET emailVerification='"+verificationKey+"' WHERE username='"+username+"' AND email='"+email+"'";
            crudInBackground.execute(sql, getString(R.string.crudURL), getString(R.string.crudApiKey));

        } else {

            SendEmail sendEmail = new SendEmail(username);
            sendEmail.execute(email, "do_not_reply@1stepsearch.com", "1Step Search - Verify Your Email Address",
                    "Hello " + username + ",<br><br>" +
                            "To verify your email address " + email + ", please go to the following link:<br>" +
                            "<a href='http://1stepsearch.com/verification/" + key + "'>http://1stepsearch.com/verification/" + key + "</a>" +
                            "<br><br>" +
                            "<small>This email was automatically generated. If you did not register for an account at 1Step Search, please disregard this email.</small>");

        }


    }


    public static String generateRandomString(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }


    public void buildDialog(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(R.drawable.logo_red)
                .show();
    }


    public class SendEmail extends AsyncTask<String, Void, String> {

        private String toEmail;
        private String username;

        private SendEmail(String username){
            this.username = username;
        }

        @Override
        protected String doInBackground(String... params) {
            toEmail = params[0];
            return downloadResponseString(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            boolean isSent = true;

            if(s == null){
                buildDialog("Failed to Send Verification Email", "Please check that you are connected to the internet.");
                isSent = false;
            }

            Intent intent = new Intent(MainActivity.this, SignupSuccessfulActivity.class);
            intent.putExtra("isSent", isSent);
            intent.putExtra("username", username);
            intent.putExtra("toEmail", toEmail);
            startActivity(intent);
            finish();
        }


        private String downloadResponseString(String to, String from, String subject, String body){
            StringBuilder tempBuffer = new StringBuilder();

            try {

                URL url = new URL(EMAIL_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                String data = Uri.encode("to", "UTF-8")+"="+Uri.encode(to, "UTF-8")+"&"+
                        Uri.encode("from", "UTF-8")+"="+Uri.encode(from, "UTF-8")+"&"+
                        Uri.encode("subject", "UTF-8")+"="+Uri.encode(subject, "UTF-8")+"&"+
                        Uri.encode("body", "UTF-8")+"="+Uri.encode(body, "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();

                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];

                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }

                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();

            } catch (IOException e ){
                Log.d("LoginPrompt", "Error 201: Could not read data. "+e.getMessage());
            } catch (SecurityException a){
                Log.d("LoginPrompt", "Error 203: Do not have the permissions to connect to URL.");

            }

            return null;
        }

    }


    public SavedSession getSavedSession(){
        SharedPreferences sp1 = this.getSharedPreferences("SavedSession", 0);
        SavedSession savedSession = new SavedSession();
        savedSession.setFname(sp1.getString("fname", null));
        savedSession.setLname(sp1.getString("lname", null));
        savedSession.setUsername(sp1.getString("username", null));
        savedSession.setEmail(sp1.getString("email", null));
        savedSession.setPhone(sp1.getString("phone", null));
        savedSession.setPlan(sp1.getString("plan", null));
        savedSession.setNumOfSearches(sp1.getInt("numOfSearches", 0));
        savedSession.setCurrentNumOfSearches(sp1.getInt("currentNumOfSearches", 0));

        return savedSession;
    }

    public void createSavedSession(SavedSession savedSession){
        SharedPreferences sp = getSharedPreferences("SavedSession", 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("fname", savedSession.getFname());
        ed.putString("lname", savedSession.getLname());
        ed.putString("username", savedSession.getUsername());
        ed.putString("email", savedSession.getEmail());
        ed.putString("phone", savedSession.getPhone());
        ed.putString("plan", savedSession.getPlan());
        ed.putInt("numOfSearches", savedSession.getNumOfSearches());
        ed.putInt("currentNumOfSearches", savedSession.getCurrentNumOfSearches());
        ed.apply();
    }






}
