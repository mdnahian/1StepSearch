package com.onestepsearch.onestepsearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
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
        String html = intent.getStringExtra("html");

        if(url != null){
//            Log.d("Crash", url);

            WebSettings webSettings = webView.getSettings();
            webSettings.setBuiltInZoomControls(true);
            webView.setWebViewClient(new Callback());
            webView.loadUrl(url);

        } else if(html != null){

            findViewById(R.id.topPanel).setVisibility(View.GONE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient());
            webView.clearCache(true);
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            webView.loadData(html, "text/html", "utf-8");
        }

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
