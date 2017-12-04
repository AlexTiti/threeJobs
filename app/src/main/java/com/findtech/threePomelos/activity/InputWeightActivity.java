package com.findtech.threePomelos.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;
import com.findtech.threePomelos.view.dialog.CustomDialog;
import com.umeng.socialize.net.utils.UResponse;

import java.util.Date;
import java.util.List;

public class InputWeightActivity extends MyActionBarActivity implements View.OnClickListener {
    EditText inputWeight;
    TextView inputWeightDate;
    Button saveWeightBtn;
    private DatepickerDialog mChangeDateDialog;
    private NetWorkRequest netWorkRequest;
    private OperateDBUtils operateDBUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_weight);
        setToolbar(getResources().getString(R.string.title_activity_input_weight), true,null);

        inputWeight = (EditText) findViewById(R.id.input_weight);
        inputWeightDate = (TextView) findViewById(R.id.txt_weight_date_input);
        saveWeightBtn = (Button) findViewById(R.id.btn_save);
        inputWeight.addTextChangedListener(textWatcher);
        inputWeightDate.setOnClickListener(this);
        saveWeightBtn.setOnClickListener(this);

        netWorkRequest = new NetWorkRequest(this);
        operateDBUtils = new OperateDBUtils(this);
        registerMusicBroadcast();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence)) {
                inputWeight.setCursorVisible(true);
            } else {
                inputWeight.setCursorVisible(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void onClick(View view) {
        if (view == inputWeightDate) {
            if (mChangeDateDialog != null && mChangeDateDialog.isShowing()) {
                return;
            }
            mChangeDateDialog = new DatepickerDialog(
                    InputWeightActivity.this);
            DatepickerDialog.setWindowSize(mChangeDateDialog);
            mChangeDateDialog.show();
            mChangeDateDialog.setBirthdayListener(new DatepickerDialog.OnBirthListener() {
                @Override
                public void onClick(String year, String month, String day) {
                    inputWeightDate.setText(year + "-" + month + "-" + day);
                }
            });
        }
        if (view == saveWeightBtn) {
            final String weight = inputWeight.getText().toString();
            final String time = inputWeightDate.getText().toString();
            if (!NetUtils.isConnectInternet(this)) {
                Toast.makeText(this, getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.valueOf(weight) > 200){
                Toast.makeText(this, getString(R.string.data_max_notice), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(weight) && !TextUtils.isEmpty(time)) {
                //判断是否是安全数据
                String heightNumStr = RequestUtils.getSharepreference(this).getString(RequestUtils.WEIGHT, "0");
                float heightNum = Float.parseFloat(heightNumStr);
                float inputHeight = Float.parseFloat(weight);
                if (heightNum - inputHeight > 1.5f || inputHeight - heightNum > 1.5f || heightNum == 0f) {
                    final CustomDialog.Builder builder = new CustomDialog.Builder(this);
                    builder.setTitle(getString(R.string.notice));
                    builder.setShowBindInfo(getString(R.string.weight_message_confirm));
                    builder.setShowButton(true);
                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int which) {
                            netWorkRequest.getProgressDialog().show();
                            saveWeightDataToServer(weight, time);
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton(getString(R.string.cancle),
                            new android.content.DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    netWorkRequest.getProgressDialog().show();
                    saveWeightDataToServer(weight, time);
                }
            } else {
                Toast.makeText(this, getString(R.string.input_time_weight), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveWeightDataToServer(final String weight, String time) {
        final Date finalDate = Tools.getDateFromTimeStr(time);
        netWorkRequest.isExistTheTimeOnTable(NetWorkRequest.BABY_WEIGHT, finalDate, new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {

                if (e == null) {
                    if (list.size() == 0) {
                        addWeightToServer(weight, finalDate);
                    } else if (list.size() == 1) {
                        updateWeightToServer(weight, finalDate);
                    } else if (list.size() > 1) {
                        AVObject.deleteAllInBackground(list, new DeleteCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    addWeightToServer(weight, finalDate);
                                } else {
                                    netWorkRequest.getProgressDialog().dismiss();
                                    Toast.makeText(InputWeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    netWorkRequest.getProgressDialog().dismiss();
                    Toast.makeText(InputWeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void addWeightToServer(final String weight, final Date date) {
        netWorkRequest.addWeightAndTimeToServer(weight, date, new SaveCallback() {
            @Override
            public void done(AVException e) {
                netWorkRequest.getProgressDialog().dismiss();
                if (e == null) {
                    finish();
                    operateDBUtils.saveWeightToDB(weight, Tools.getTimeFromDate(date));
                } else {
                    Toast.makeText(InputWeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateWeightToServer(final String weight, final Date date) {

        netWorkRequest.updateWeightAndTimeToServer(weight, date, new SaveCallback() {
            @Override
            public void done(AVException e) {
                netWorkRequest.getProgressDialog().dismiss();
                if (e == null) {
                    finish();
                    operateDBUtils.saveWeightToDB(weight, Tools.getTimeFromDate(date));
                } else {
                    Toast.makeText(InputWeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
