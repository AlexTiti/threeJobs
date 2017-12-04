package com.findtech.threePomelos.activity;

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
 * @author Administrator
 *
 */

public class FunctionDescActivity extends MyActionBarActivity {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private String url = "https://dn-1r2os0w0.qbox.me/af64803e1dab670e5a51.html";

    private final int progress_compete = 100;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_desc);
        registerMusicBroadcast();
        setToolbar(getResources().getString(R.string.function_introduce),true,null);
        mWebView = (WebView) findViewById(R.id.webView_func);
        mProgressBar= (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setBackgroundColor(getResources().getColor(R.color.white));
        mProgressBar.setMax(100);

//        mWebView.setWebViewClient(new WebViewClient(){
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });

        mWebView.setWebViewClient(new WebViewClient(){
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
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == progress_compete){
                    mProgressBar.setVisibility(View.GONE);
                }else {
                    mProgressBar.setProgress(newProgress);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        mWebView.loadUrl(url);
    }
}
