package com.findtech.threePomelos.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.service.RFStarBLEService;

import java.io.UnsupportedEncodingException;

/**
 * 获取数据
 * 
 * @author kevin
 * 
 */
public class ReceivedataActivity extends MyActionBarActivity implements
		OnClickListener, OnCheckedChangeListener, BLEDevice.RFStarBLEBroadcastReceiver {
	private CheckBox checkBox = null;
	private Button resetBtn = null;
	private ReceiveDataView receiveDataView = null;

	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_getdata);
		this.initView();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (app.manager.cubicBLEDevice != null)
			app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
	}

	@SuppressWarnings("static-access")
	private void initView() {
		// TODO Auto-generated method stub
		checkBox = (CheckBox) this.findViewById(R.id.checkBox1);
		checkBox.setOnCheckedChangeListener(this);
		resetBtn = (Button) this.findViewById(R.id.resetBtn);
		resetBtn.setOnClickListener(this);

		receiveDataView = (ReceiveDataView) this
				.findViewById(R.id.getDataView1);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == resetBtn) {
			receiveDataView.reset();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		receiveDataView.changeEditBackground(isChecked);
		if (app.manager.cubicBLEDevice != null) {
			Log.d(MyApplication.TAG, "isChecked = " + isChecked);
			app.manager.cubicBLEDevice.setNotification(
					isChecked);
		}
	}

	/**
	 * 返回数据
	 */
	@Override
	public void onReceive(Context context, Intent intent, String macData,
			String uuid) {
		Log.d(MyApplication.TAG, "有数据返回");
		// TODO Auto-generated method stub
		String action = intent.getAction();
		this.connectedOrDis(intent.getAction());
		if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
			if (uuid.contains("ffe4")) {
				byte[] data = intent
						.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
				try {
					receiveDataView.setCountTimesTxt();
					receiveDataView.appendString(new String(data, "GB2312"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
				.equals(action)) {

		}
	}

	@Override
	public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {

	}
}
