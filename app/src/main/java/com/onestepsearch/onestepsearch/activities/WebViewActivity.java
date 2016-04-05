package com.onestepsearch.onestepsearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.onestepsearch.onestepsearch.R;


/**
 * Created by mdislam on 3/22/16.
 */
public class WebViewActivity extends Activity {

    private ImageView backBtn;
    private TextView title;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        title = (TextView) findViewById(R.id.title);
        webView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();
        String strTitle = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");

        webView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new Callback());
        webView.loadUrl(url);

        title.setText(strTitle);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private class Callback extends WebViewClient {  //HERE IS THE MAIN CHANGE.

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }

    }


}
