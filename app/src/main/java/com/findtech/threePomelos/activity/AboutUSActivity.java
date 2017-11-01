package com.findtech.threePomelos.activity;

import android.os.Bundle;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;

/**
 * @author Administrator
 */
public class AboutUSActivity extends MyActionBarActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        registerMusicBroadcast();
        setToolbar(getResources().getString(R.string.about_us),true,null);
    }

}
