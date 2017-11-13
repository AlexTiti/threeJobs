package com.findtech.threePomelos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.utils.Tools;

/**
 * @author Administrator
 *
 */
public class AboutUSActivity extends MyActionBarActivity implements View.OnClickListener {

    private RelativeLayout relay_approve_us ,about_us,relayout_func_us;
    private TextView text_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        registerMusicBroadcast();
        setToolbar(getResources().getString(R.string.about_us),true,null);
        text_code = (TextView) findViewById(R.id.text_code);
        text_code.setText(getString(R.string.xliff_current_code,Tools.getCurrentVersion(this)));
        relay_approve_us = (RelativeLayout) findViewById(R.id.relay_approve_us);
        relayout_func_us = (RelativeLayout) findViewById(R.id.relayout_func_us);
        about_us = (RelativeLayout) findViewById(R.id.about_us);
        relay_approve_us.setOnClickListener(this);
        relayout_func_us.setOnClickListener(this);
        about_us.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relay_approve_us:
                toGoGetProtectActivity(this);
                break;
            case R.id.relayout_func_us:
                startActivity(new Intent(this,FunctionDescActivity.class));
                break;
            case R.id.about_us:
                startActivity(new Intent(this,CompanyActivity.class));
                break;
            default:
                break;
        }
    }
}
