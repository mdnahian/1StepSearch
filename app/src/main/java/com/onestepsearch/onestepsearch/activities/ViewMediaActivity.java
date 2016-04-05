package com.onestepsearch.onestepsearch.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by mdislam on 12/28/15.
 */
public class ViewMediaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String uri = intent.getExtras().getString("uri");

        Intent newIntent = new Intent();
        newIntent.setAction(Intent.ACTION_VIEW);

        if(uri.substring(uri.length() - 3).equals("mp4") || uri.substring(uri.length() - 3).equals("mp3")){
            newIntent.setDataAndType(Uri.parse(uri), "video/*");
        } else if(uri.substring(uri.length() - 3).equals("pdf")) {
            newIntent.setDataAndType(Uri.parse("file://"+uri), "application/pdf");
        } else {
            newIntent.setDataAndType(Uri.parse("file://"+uri), "image/*");
        }

        Intent chooseIntent = Intent.createChooser(newIntent, "Open File");

        try {
            startActivity(chooseIntent);
        } catch (ActivityNotFoundException e){
            Log.d("Crash", "Could not handle file.");
        }

        finish();
    }


}
