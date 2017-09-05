package com.findtech.threePomelos.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.findtech.threePomelos.base.MyActionBarActivity;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;

public class InputIdentifyActivity extends MyActionBarActivity implements View.OnClickListener {

    private EditText identifyTextView;
    private Button nextButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);
        setContentView(R.layout.activity_input_identify);
        setToolbar(getResources().getString(R.string.title_activity_input_identify), true,null);

        identifyTextView = (EditText) findViewById(R.id.input_identify);
        nextButton = (Button) findViewById(R.id.next_step);
        nextButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                String identify = identifyTextView.getText().toString();
                if (TextUtils.isEmpty(identify)) {
                    Toast toast = Toast.makeText(this, R.string.input_identify, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent intent = getIntent();
                    if (intent != null) {
                        String phoneStr = intent.getExtras().getString("phone");

                    }
                }
                break;
        }
    }
}
