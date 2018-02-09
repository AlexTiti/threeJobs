package com.findtech.threePomelos.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.BaseActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.home.MainHomeActivity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.net.QueryBabyInfoCallBack;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText phoneTextView;
    private EditText passwordTextView;
    private Button loginButton ;
    private TextView registerButton,findPassword,login_ckick_notice;
    private ImageView weiXinLogin, QQLogin, weiBoLogin;
    private RelativeLayout deletePassword, deleteUser;
    private String phoneNum;
    private ThirdPartyController thirdPartyController;
    private OperateDBUtils operateDBUtils;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_login);
        thirdPartyController = new ThirdPartyController(this);
        progressDialog = thirdPartyController.getProgressDialog();
        initView();

        operateDBUtils = new OperateDBUtils(this);

        phoneTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String patternStr = "\\s+";
                String replaceStr = "";
                Pattern pattern = Pattern.compile(patternStr);
                Matcher matcher = pattern.matcher(charSequence);
                phoneNum = matcher.replaceAll(replaceStr);
//                if (!TextUtils.isEmpty(charSequence)) {
//                    loginButton.setEnabled(true);
//                } else {
//                    loginButton.setEnabled(false);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void initView() {
        phoneTextView = (EditText) findViewById(R.id.inputnum);
        passwordTextView = (EditText) findViewById(R.id.password);
        passwordTextView.setTypeface(Typeface.SANS_SERIF);

        loginButton = (Button) findViewById(R.id.login);
        registerButton = (TextView) findViewById(R.id.register);
        findPassword = (TextView) findViewById(R.id.find_password);
        deleteUser = (RelativeLayout) findViewById(R.id.deleteUser);
        deletePassword = (RelativeLayout) findViewById(R.id.deletePassword);
        login_ckick_notice = (TextView) findViewById(R.id.login_ckick_notice);
        weiXinLogin = (ImageView) findViewById(R.id.wx_login);
        QQLogin = (ImageView) findViewById(R.id.wb_login);
        weiBoLogin = (ImageView) findViewById(R.id.qq_login);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        findPassword.setOnClickListener(this);
        deleteUser.setOnClickListener(this);
        login_ckick_notice.setOnClickListener(this);
        deletePassword.setOnClickListener(this);
        weiXinLogin.setOnClickListener(this);
        QQLogin.setOnClickListener(this);
        weiBoLogin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.register:
                Intent intent = new Intent(this, PhoneVerifyActivity.class);
                startActivity(intent);
                break;
            case R.id.find_password:
                Intent findPasswordIntent = new Intent(this, ForgetPasswordActivity.class);
                startActivity(findPasswordIntent);
                break;
            case R.id.deleteUser:
                if (phoneTextView != null) phoneTextView.setText("");
                break;
            case R.id.deletePassword:
                if (passwordTextView != null) passwordTextView.setText("");
                break;
            case R.id.wx_login:
                if (thirdPartyController.getUMWXHandler().isClientInstalled()) {
                    thirdPartyController.ThirdPartyLogin(SHARE_MEDIA.WEIXIN);
                } else {
                    ToastUtil.showToast(this, getResources().getString(R.string.install_wx));
                }
                break;
            case R.id.wb_login:
                thirdPartyController.ThirdPartyLogin(SHARE_MEDIA.SINA);
                break;
            case R.id.qq_login:
                try {
                    if (thirdPartyController.isQQClientInstalled()) {
                        thirdPartyController.ThirdPartyLogin(SHARE_MEDIA.QQ);
                    } else {
                        ToastUtil.showToast(this, getResources().getString(R.string.install_qq));
                    }
                } catch (Exception e) {

                }

                break;
            case R.id.login_ckick_notice:

                toGoGetProtectActivity(LoginActivity.this);

                break;
        }
    }








    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!MyApplication.DEBUG) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                MyApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void login() {
        String password = passwordTextView.getText().toString();

        if (!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(password)) {
            progressDialog.show();
            AVUser.logInInBackground(phoneNum, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        NetWorkRequest netWorkRequest = new NetWorkRequest(LoginActivity.this);
                        netWorkRequest.getTravelInfoDataAndSaveToDB();
                        netWorkRequest.getTotalMileageDataAndSaveToSP();
                        netWorkRequest.getBabyWeightDataAndSaveToDB();
                        netWorkRequest.getBabyHeightDataAndSaveToDB();
                        netWorkRequest.getBabyInfoDataAndSaveToDB(new QueryBabyInfoCallBack.QueryIsBind() {

                            @Override
                            public void finishQueryIsBind(boolean isBind, String deviceId) {


                                RequestUtils.getSharepreferenceEditor(LoginActivity.this)
                                        .putBoolean(RequestUtils.IS_LOGIN, true).apply();
                                if (isBind) {
                                    startActivity(new Intent(LoginActivity.this, MainHomeActivity.class));
                                    progressDialog.dismiss();
                                } else {
                                    startActivity(new Intent(LoginActivity.this, MainHomeActivity.class));
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        switch (e.getCode()) {
                            case 210:
                                ToastUtil.showToast(LoginActivity.this, getResources().getString(R.string.login_notice));
                                break;
                            case 211:
                                ToastUtil.showToast(LoginActivity.this, getResources().getString(R.string.name_useless));
                                break;
                        }
                        if (e.getMessage().equals("java.net.UnknownHostException")) {
                            checkNetWork();
                        }
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ToastUtil.showToast(this, getResources().getString(R.string.login_empty_notice));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = thirdPartyController.getUMSocialServiceLoginInstance().
                getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        phoneTextView.setText("");
        passwordTextView.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
