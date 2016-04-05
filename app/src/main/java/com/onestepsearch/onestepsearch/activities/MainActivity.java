package com.onestepsearch.onestepsearch.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.onestepsearch.onestepsearch.R;

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


    public void sendVerificationEmail(final String userId, final String email){

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.getInBackground(userId, new GetCallback<ParseUser>() {
//            boolean isSent = false;
//
//            @Override
//            public void done(ParseUser user, ParseException e) {
//                if (user.getEmail().equals(email)) {
//                    String verificationKey = generateRandomString();
//                    user.put("verificationKey", verificationKey);
//                    user.saveEventually();
//
//                    SendEmail sendEmail = new SendEmail();
//                    sendEmail.execute(email, "do_not_reply@1stepsearch.com", "1Step Search - Verify Your Email Address",
//                            "Hello " + user.get("fname") + ",<br><br>" +
//                                    "To verify your email address " + user.getEmail() + ", please go to the following link:<br>" +
//                                    "<a href='http://1stepsearch.com/verification/" + verificationKey + "'>http://1stepsearch.com/verification/" + verificationKey + "</a>" +
//                                    "<br><br>" +
//                                    "<small>This email was automatically generated. If you did not register for an account at 1Step Search, please disregard this email.</small>");
//                    isSent = true;
//
//                    Intent intent = new Intent(MainActivity.this, SignupSuccessfulActivity.class);
//                    intent.putExtra("userId", userId);
//                    intent.putExtra("toEmail", email);
//                    startActivity(intent);
//
//                } else {
//                    isSent = false;
//                }
//
//                if (!isSent) {
//                    buildDialog("Failed to Send Verification Email", "Are you sure you are a registered user?");
//                }
//            }
//        });



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

        @Override
        protected String doInBackground(String... params) {
            return downloadResponseString(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s == null){
                buildDialog("Failed to Send Verification Email", "Are you sure you are a registered user?");
            }
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

}
