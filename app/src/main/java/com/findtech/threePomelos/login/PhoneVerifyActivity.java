package com.findtech.threePomelos.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;

/**
 * @author Alex
 */
public class PhoneVerifyActivity extends MyActionBarActivity implements View.OnClickListener {

    private TextView mIdentify;
    private Button nextButton;

    private EditText password;
    private RelativeLayout mIsShowPassword;
    private ImageView showPassword, hidePassword;

    private EditText phoneTextView;
    private EditText phoneIdentifyTextView;
    private String correctPhoneNum;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_phone_verify);
        setToolbar(getResources().getString(R.string.phone_verify), true, null);

        phoneTextView = (EditText) findViewById(R.id.input_num);
        phoneIdentifyTextView = (EditText) findViewById(R.id.identify_num);
        password = (EditText) findViewById(R.id.input_password);
        mIsShowPassword = (RelativeLayout) findViewById(R.id.is_show_password);
        showPassword = (ImageView) findViewById(R.id.show_password);
        hidePassword = (ImageView) findViewById(R.id.hide_password);
        mIsShowPassword.setOnClickListener(this);

        mIdentify = (TextView) findViewById(R.id.send_identify);
        nextButton = (Button) findViewById(R.id.next);

        mIdentify.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        phoneTextView.setText("");
        phoneIdentifyTextView.setText("");
    }

    @Override
    public void onClick(View view) {
        String phoneStr = phoneTextView.getText().toString();
        switch (view.getId()) {
            case R.id.send_identify:
                if (checkLegalUser(phoneStr)) {
                    correctPhoneNum = phoneStr;
                    AVOSCloud.requestSMSCodeInBackground(phoneStr, getString(R.string.app_name), null, 2, new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                timer.start();
                                Toast.makeText(getApplicationContext(), getString(R.string.checkNum_notice), Toast.LENGTH_SHORT).show();
                            } else {
                                if (e.getCode() == 601) {
                                    ToastUtil.showToast(PhoneVerifyActivity.this, getResources().getString(R.string.checkNumber_send_fail));
                                }
                              checkNetWork();
                            }
                        }
                    });
                }
                break;
            case R.id.next:
                    String identifyCode = phoneIdentifyTextView.getText().toString();
                String psd = password.getText().toString();
                    if (!checkLegalUser(phoneStr)) {
                        return;
                    }
                    if (TextUtils.isEmpty(identifyCode)) {
                        ToastUtil.showToast(this, R.string.hint_identify_text);
                        return;
                    }

                if (TextUtils.isEmpty(psd)) {
                    ToastUtil.showToast(this, R.string.input_password);
                    return;
                }

                    if (!TextUtils.isEmpty(correctPhoneNum)) {
                        verifySMSCode(identifyCode, correctPhoneNum);

                }
                break;

            case R.id.is_show_password:
                if (showPassword.getVisibility() == View.GONE &&
                        hidePassword.getVisibility() == View.VISIBLE) {
                    showPassword.setVisibility(View.VISIBLE);
                    hidePassword.setVisibility(View.GONE);
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.requestFocus();
                    password.setSelection(password.getText().length());
                } else if (showPassword.getVisibility() == View.VISIBLE &&
                        hidePassword.getVisibility() == View.GONE) {
                    showPassword.setVisibility(View.GONE);
                    hidePassword.setVisibility(View.VISIBLE);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.requestFocus();
                    password.setSelection(password.getText().length());
                } else {
                    showPassword.setVisibility(View.GONE);
                    hidePassword.setVisibility(View.VISIBLE);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.requestFocus();
                    password.setSelection(password.getText().length());
                }

                break;
        }
    }

    private void verifySMSCode(String code, String phone) {

        AVOSCloud.verifySMSCodeInBackground(code, phone, new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    RequestUtils.getSharepreferenceEditor(PhoneVerifyActivity.this)
                            .putString(RequestUtils.USERNAME, correctPhoneNum).commit();

                    String passwordStr = password.getText().toString();
                    if (TextUtils.isEmpty(passwordStr)) {
                        ToastUtil.showToast(PhoneVerifyActivity.this, R.string.input_password);
                    } else {
                        MyApplication.setPassword(passwordStr);
                        Intent intent = new Intent(PhoneVerifyActivity.this, SetBabySexActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (e.getCode() == 603) {
                        ToastUtil.showToast(PhoneVerifyActivity.this, getResources().getString(R.string.checkNumber_useless));
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkLegalUser(String phoneStr) {
        boolean canSendRequest = true;
        if (TextUtils.isEmpty(phoneStr)) {
            canSendRequest = false;
            ToastUtil.showToast(this, R.string.input_phone_number);
        } else if (!phoneStr.isEmpty() && !phoneStr.matches("^(13|14|15|17|18)\\d{9}$")) {
            canSendRequest = false;
            ToastUtil.showToast(this, R.string.input_legal_phone_number);
        }
        return canSendRequest;
    }

    CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            mIdentify.setBackground(getResources().getDrawable(R.drawable.forget_psd_bac));
            mIdentify.setEnabled(false);
            mIdentify.setText(millisUntilFinished/1000 + "s");
        }

        @Override
        public void onFinish() {
            mIdentify.setBackground(getResources().getDrawable(R.drawable.login_button_selector));
            mIdentify.setEnabled(true);
            mIdentify.setText(getString(R.string.identify));
        }
    };




}
