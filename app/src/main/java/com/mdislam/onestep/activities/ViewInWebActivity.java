package com.mdislam.onestep.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.mdislam.onestep.R;

/**
 * Created by mdislam on 1/26/16.
 */
public class ViewInWebActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_in_web_activity);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(url);

        finish();

    }

}
