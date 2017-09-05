package com.findtech.threePomelos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.text.ParseException;
import java.util.List;

/**
 * Created by zhi.zhang on 5/3/16.
 */
public class GetUserProtocolActivity extends MyActionBarActivity {

    //  private TextView userProtocolContent;
    private WebView webView_user_protect;
    private ProgressDialog progressDialog;
    private String protect_url;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_protocol);


        setToolbar(getResources().getString(R.string.title_activity_get_user_protocol), true, null);
        Intent intent = getIntent();
        if (intent != null)
            protect_url = intent.getStringExtra("protect_url");
        progressBar = (ProgressBar) findViewById(R.id.pb_user_protect);
        progressBar.setBackgroundColor(getResources().getColor(R.color.white));
        progressBar.setMax(100);
        webView_user_protect = (WebView) findViewById(R.id.webView_user_protect);
        webView_user_protect.loadUrl(protect_url);
        webView_user_protect.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });

        webView_user_protect.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress == 100)
                    progressBar.setVisibility(View.GONE);
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });

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

//    Handler mHandle = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what == 0x75) {
////                userProtocolContent.setText(Html.fromHtml((String) msg.obj));
//            }
//        }
//    };

//    private void initProgressDialog() {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage(getResources().getString(R.string.getMessage_fromNet));
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(true);
//        progressDialog.setCanceledOnTouchOutside(true);
//    }
//
//    private void getUserProtocol() {
//        progressDialog.show();
//        AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.USER_PROTOCOL);
//        query.findInBackground(new FindCallback<AVObject>() {
//            public void done(List<AVObject> avObjects, AVException e) {
//                if (e == null) {
//                    if (avObjects.size() > 0) {
//                        AVFile avFile = avObjects.get(1).getAVFile("userProtocol");
//                        if (avFile == null) {
//                            ToastUtil.showToast(GetUserProtocolActivity.this, getResources().getString(R.string.data_exception));
//                            return;
//                        }
//                        avFile.getDataInBackground(new GetDataCallback() {
//                            public void done(byte[] data, AVException e) {
//                                //process data or exception.
//                                progressDialog.dismiss();
//                                if (e == null) {
//                                    String body = new String(data);
//                                    mHandle.obtainMessage(0x75, body).sendToTarget();
//                                } else {
//                                    checkNetWork();
//                                }
//                            }
//                        }, new ProgressCallback() {
//                            @Override
//                            public void done(Integer integer) {
//                                mHandle.obtainMessage(0x74, integer).sendToTarget();
//                            }
//                        });
//                    } else {
//                        progressDialog.dismiss();
//                        ToastUtil.showToast(GetUserProtocolActivity.this, getResources().getString(R.string.no_data));
//                    }
//                } else {
//                    progressDialog.dismiss();
//                    checkNetWork();
//                }
//            }
//        });
//    }
}
