package com.findtech.threePomelos.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;

/**
 * @author Alex
 * @date 2017/11/07
 * @desc to show Company
 */
public class CompanyActivity extends MyActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        registerMusicBroadcast();
        setToolbar(getResources().getString(R.string.company_introduce),true,null);
    }
}
