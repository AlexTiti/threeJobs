package com.findtech.threePomelos.mydevices.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;


public class RepairActivity extends MyActionBarActivity implements BLEDevice.RFStarBLEBroadcastReceiver  {
    private View mButtom;

    private View btn_repair_repair;
    private TextView text_state_repair;
    private NetWorkRequest netWorkRequest;
    private ProgressDialog progressDialog_0;
    private String notice ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair);
        setToolbar(getResources().getString(R.string.repair),true,null);
        mButtom = findViewById(R.id.btn_repair_repair);
        notice = getString(R.string.repair_fail);
        text_state_repair = (TextView) findViewById(R.id.text_state_repair);
        btn_repair_repair = findViewById(R.id.btn_repair_repair);
        netWorkRequest = new NetWorkRequest(this);
        mButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!IContent.getInstacne().isBind){
                    ToastUtil.showToast(RepairActivity.this,getString(R.string.device_nolink));
                    return;
                }
                showProgressDialog(getString(R.string.repairing) );
                if (  app.manager.cubicBLEDevice != null) {
                    app.manager.cubicBLEDevice.setBLEBroadcastDelegate(RepairActivity.this);
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.REPAIR_CAR);
                }
            }
        });

    }

    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
       if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {
            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
           if (data[3] == (byte) 0x83 ) {
               doMusic(data);
           }
            if (data[3] == (byte) 0x8B && data[4] == 0x55) {
                String message = Tools.byte2Hex(data);

                netWorkRequest.sendRepairMessage(message, Tools.getCurrentDate(), IContent.getInstacne().address, IContent.getInstacne().clickPositionType, new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        doSuccess();
                    }
                });
            }else{
                ToastUtil.showToast(RepairActivity.this,getString(R.string.repair_fail));
            }
        }
    }
    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {

    }
    private void doSuccess(){
        dismissProgressDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog_0.isShowing()) {
                    progressDialog_0.dismiss();
                }
                btn_repair_repair.setBackgroundResource(R.drawable.btn_repare_done);
                text_state_repair.setText(getResources().getString(R.string.repair_done));
                btn_repair_repair.setClickable(false);
                ToastUtil.showToast(RepairActivity.this,getString(R.string.repair_done));
                finish();


            }
        },3000);

    }


    public void showProgressDialog(String message ) {
        progressDialog_0 = new ProgressDialog(this);
        progressDialog_0.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog_0.setMessage(message);
        progressDialog_0.setIndeterminate(false);
        progressDialog_0.setCancelable(true);
        progressDialog_0.setCanceledOnTouchOutside(false);
        progressDialog_0.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressDialog_0.isShowing()) {
                    progressDialog_0.dismiss();
                    ToastUtil.showToast(RepairActivity.this,getString(R.string.repair_fail));
                }
            }
        },8000);

    }



}
