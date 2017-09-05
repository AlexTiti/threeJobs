package com.findtech.threePomelos.activity;

import android.os.Bundle;
import android.view.View;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.view.arcview.UpdateCircleView;

public class FirmwareUpdateActivity extends MyActionBarActivity implements View.OnClickListener {
    UpdateCircleView mUpdateCircleView;
    int progress = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);

        mUpdateCircleView = (UpdateCircleView) findViewById(R.id.update_circle_panel);
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (progress <= 100) {
                    progress += 3;
                    mUpdateCircleView.setCurrentProgress(progress);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        registerMusicBroadcast();
    }

    @Override
    protected void onResume() {
        super.onResume();
        colorChange("53d9d7", false);
    }

    @Override
    public void onClick(View v) {

    }
}
