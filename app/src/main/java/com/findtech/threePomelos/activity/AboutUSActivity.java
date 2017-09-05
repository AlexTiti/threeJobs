package com.findtech.threePomelos.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
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
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.text.ParseException;
import java.util.List;

public class AboutUSActivity extends MyActionBarActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        registerMusicBroadcast();
        setToolbar(getResources().getString(R.string.about_us),true,null);
    }

}
