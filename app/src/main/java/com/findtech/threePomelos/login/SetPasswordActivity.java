package com.findtech.threePomelos.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.ToastUtil;

public class SetPasswordActivity extends MyActionBarActivity implements View.OnClickListener {

    private EditText password;
    private Button nextButton;
    private RelativeLayout mIsShowPassword;
    private ImageView showPassword, hidePassword;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_set_password);
        setToolbar(getResources().getString(R.string.title_activity_set_password), true,null);
        password = (EditText) findViewById(R.id.input_password);
        nextButton = (Button) findViewById(R.id.next_step);
        mIsShowPassword = (RelativeLayout) findViewById(R.id.is_show_password);
        showPassword = (ImageView) findViewById(R.id.show_password);
        hidePassword = (ImageView) findViewById(R.id.hide_password);
        nextButton.setOnClickListener(this);
        mIsShowPassword.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_step:
                String passwordStr = password.getText().toString();
                if (TextUtils.isEmpty(passwordStr)) {
                    ToastUtil.showToast(this, R.string.input_password);
                } else {
                    MyApplication.setPassword(passwordStr);
                    Intent intent = new Intent(this, SetBabySexActivity.class);
                    startActivity(intent);
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
}
