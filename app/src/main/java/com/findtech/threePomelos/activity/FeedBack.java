package com.findtech.threePomelos.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FeedBack extends MyActionBarActivity {

    private EditText feedback;
    private Button btn_send;
    private NetWorkRequest netWorkRequest;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        setToolbar(getResources().getString(R.string.faceback), true, null);
        feedback = (EditText) findViewById(R.id.text_feed);
        btn_send = (Button) findViewById(R.id.btn_send);
        netWorkRequest = new NetWorkRequest(this);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = feedback.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast(FeedBack.this, getResources().getString(R.string.name_not));
                    return;
                }
                showProgressDialog(getResources().getString(R.string.sendMessage_),getString(R.string.sendMessage_fail));
                netWorkRequest.sendFeedBackToServer(content, Tools.getCurrentDateHour(), babyInfoEntity.getBabyName(), new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        dismissProgressDialog();
                        if (e == null) {
                            ToastUtil.showToast(FeedBack.this, getResources().getString(R.string.send_sucess));
                            finish();
                        } else {
                            checkNetWork();
                        }

                    }
                });
            }
        });
        registerMusicBroadcast();
    }
}
