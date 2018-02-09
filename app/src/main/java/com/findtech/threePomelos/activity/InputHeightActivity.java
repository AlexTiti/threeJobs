package com.findtech.threePomelos.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;

import java.util.Date;
import java.util.List;

public class InputHeightActivity extends MyActionBarActivity implements View.OnClickListener {
    EditText inputHeight;
    TextView inputHeightDate;
    Button saveHeightBtn;
    private DatepickerDialog mChangeDateDialog = null;
    private NetWorkRequest netWorkRequest;
    private OperateDBUtils operateDBUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_height);
        setToolbar(getResources().getString(R.string.title_activity_input_height), true, null);

        inputHeight = (EditText) findViewById(R.id.input_Height);
        inputHeightDate = (TextView) findViewById(R.id.txt_height_date_input);
        saveHeightBtn = (Button) findViewById(R.id.btn_save);
        inputHeight.addTextChangedListener(textWatcher);
        inputHeightDate.setOnClickListener(this);
        saveHeightBtn.setOnClickListener(this);

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
                inputHeight.setCursorVisible(true);
            } else {
                inputHeight.setCursorVisible(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    public void onClick(View view) {
        if (view == inputHeightDate) {
            if (mChangeDateDialog != null && mChangeDateDialog.isShowing()) {
                return;
            }
            mChangeDateDialog = new DatepickerDialog(
                    InputHeightActivity.this);
            DatepickerDialog.setWindowSize(mChangeDateDialog);
            mChangeDateDialog.show();
            mChangeDateDialog.setBirthdayListener(new DatepickerDialog.OnBirthListener() {
                @Override
                public void onClick(String year, String month, String day) {
                    inputHeightDate.setText(year + "-" + month + "-" + day);
                }
            });
        }
        if (view == saveHeightBtn) {
            final String height = inputHeight.getText().toString();
            final String time = inputHeightDate.getText().toString();
            if (!NetUtils.isConnectInternet(this)) {
                Toast.makeText(this, getString(R.string.net_exception), Toast.LENGTH_SHORT).show();
                return;
            }
            if ( TextUtils.isEmpty(height) || Double.valueOf(height) > 200){
                Toast.makeText(this, getString(R.string.data_max_notice), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.isEmpty(height) && !TextUtils.isEmpty(time)) {
                netWorkRequest.getProgressDialog().show();

                final Date finalDate = Tools.getDateFromTimeStr(time);
                netWorkRequest.isExistTheTimeOnTable(NetWorkRequest.BABY_HEIGHT, finalDate, new FindCallback() {
                    @Override
                    public void done(List list, AVException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                addHeightToServer(height, finalDate);
                            } else if (list.size() == 1) {
                                updateHeightToServer(height, finalDate);
                            } else if (list.size() > 1) {
                                AVObject.deleteAllInBackground(list, new DeleteCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            addHeightToServer(height, finalDate);
                                        } else {
                                            Toast.makeText(InputHeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(InputHeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, getString(R.string.input_time_height), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addHeightToServer(final String height, final Date date) {
        netWorkRequest.addHeightAndTimeToServer(height, date, new SaveCallback() {
            @Override
            public void done(AVException e) {
                netWorkRequest.getProgressDialog().dismiss();
                if (e == null) {
                    finish();
                    operateDBUtils.saveHeightToDB(height, Tools.getTimeFromDate(date));
                } else {
                    Toast.makeText(InputHeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateHeightToServer(final String height, final Date date) {

        netWorkRequest.updateHeightAndTimeToServer(height, date, new SaveCallback() {
            @Override
            public void done(AVException e) {
                netWorkRequest.getProgressDialog().dismiss();
                if (e == null) {
                    finish();
                    operateDBUtils.saveHeightToDB(height, Tools.getTimeFromDate(date));
                } else {
                    Toast.makeText(InputHeightActivity.this, getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
