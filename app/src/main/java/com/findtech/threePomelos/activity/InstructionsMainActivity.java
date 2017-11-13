package com.findtech.threePomelos.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Alex
 */
public class InstructionsMainActivity extends MyActionBarActivity implements View.OnClickListener{

    NetWorkRequest netWorkRequest;
    LinearLayout linearLayout;
    private ProgressBar progressBar;
    WebView webView;
    String instructions , url;
    TextView text_common ,text_instruction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_main);
        setToolbar( getString(R.string.instructions),true,null);
        text_common = (TextView) findViewById(R.id.text_common);
        text_instruction = (TextView) findViewById(R.id.text_instruction);
        progressBar = (ProgressBar) findViewById(R.id.pb_user_protect);
        progressBar.setBackgroundColor(getResources().getColor(R.color.white));
        progressBar.setMax(100);
        text_common.setOnClickListener(this);
        text_instruction.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            instructions = intent.getStringExtra("url0");
            url = intent.getStringExtra("url1");
        }

        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(instructions);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_common:
                text_common.setTextColor(getResources().getColor(R.color.text_pink));
                text_instruction.setTextColor(getResources().getColor(R.color.text_grey));
                webView.loadUrl(instructions);
                break;
            case R.id.text_instruction:
                text_common.setTextColor(getResources().getColor(R.color.text_grey));
                text_instruction.setTextColor(getResources().getColor(R.color.text_pink));
                webView.loadUrl(url);
                break;
        }
    }




}
