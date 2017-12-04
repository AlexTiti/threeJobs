package com.findtech.threePomelos.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;

/**
 *
 * @author zhi.zhang
 * @date 5/3/16
 */
public class GetUserProtocolActivity extends MyActionBarActivity {

    private WebView webView_user_protect;
    private String protect_url;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_protocol);


        setToolbar(getResources().getString(R.string.title_activity_get_user_protocol), true, null);
        Intent intent = getIntent();
        if (intent != null) {
            protect_url = intent.getStringExtra("protect_url");
        }
        progressBar = (ProgressBar) findViewById(R.id.pb_user_protect);
        progressBar.setBackgroundColor(getResources().getColor(R.color.white));
        progressBar.setMax(100);
        webView_user_protect = (WebView) findViewById(R.id.webView_user_protect);

        webView_user_protect.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString());
                } else {
                    view.loadUrl(request.toString());
                }
                return true;
            }
        });

        webView_user_protect.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webView_user_protect.loadUrl(protect_url);
        registerMusicBroadcast();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


}
