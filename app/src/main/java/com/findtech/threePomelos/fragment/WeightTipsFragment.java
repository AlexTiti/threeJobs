package com.findtech.threePomelos.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.BabyInfoActivity;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.text.ParseException;
import java.util.List;

public class WeightTipsFragment extends Fragment implements View.OnClickListener {

    private RelativeLayout babyHealthTip;
    private TextView tipsTitle, tipsContent;
    private BabyInfoEntity babyInfoEntity = BabyInfoEntity.getInstance();
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight_tips, container, false);
        tipsTitle = (TextView) view.findViewById(R.id.tips_title);
        tipsContent = (TextView) view.findViewById(R.id.tips_content);
        requestHealthTipsPreviewAndUpdateUI();
        if (getString(R.string.input_birth_baby).equals(babyInfoEntity.getBirthday())) {
            tipsContent.setText(getString(R.string.text_health));
        } else {
            tipsContent.setText(RequestUtils.getSharepreference(getContext()).getString(RequestUtils.TIPS_PREVIEW,
                    getString(R.string.text_health)));
        }
        babyHealthTip = (RelativeLayout) view.findViewById(R.id.baby_health_tip);
        babyHealthTip.setOnClickListener(this);
        final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
        if (babyInfoEntity.getBirthday() == null) {
            return view;
        }
        if (!getString(R.string.input_birth_baby).equals(babyInfoEntity.getBirthday())) {

            String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
            try {
                String tipsStr = new MyCalendar(birthday, currentDate,getActivity()).getDateForHealthTipsStr();
                tipsTitle.setText(String.format(tipsStr + "%s", getString(R.string.tips)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tipsTitle.setText(getString(R.string.tips));
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == babyHealthTip) {
            if (getString(R.string.input_birth_baby).equals(babyInfoEntity.getBirthday())) {
                gotoBabyInfoViewDialog();
            } else {
                Intent intent = new Intent();
                getActivity().setResult(Activity.RESULT_OK,intent);
                getActivity().finish();
            }
        }
    }

    private void gotoBabyInfoViewDialog() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getString(R.string.notice));
        builder.setNotifyInfo(getString(R.string.input_birth_baby));

        builder.setShowButton(true);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(getContext(), BabyInfoActivity.class));
            }
        });

        builder.setNegativeButton(getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x78) {
                if (isAdded()) {
                    tipsContent.setText((String) msg.obj);
                    RequestUtils.getSharepreferenceEditor(getContext()).putString(RequestUtils.TIPS_PREVIEW,
                            (String) msg.obj).commit();
                }
            }
        }
    };

    private void requestHealthTipsPreviewAndUpdateUI() {
        MyCalendar myCalendar;
        try {
            final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
            if (TextUtils.isEmpty(babyInfoEntity.getBirthday())) {
                return;
            }
            String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
            babyInfoEntity.setBabyTotalDay(getContext(), birthday, "0");
            myCalendar = new MyCalendar(birthday, currentDate,getActivity());

            AVQuery<AVObject> query = new AVQuery<>(NetWorkRequest.HEALTH_TIPS);
            query.whereEqualTo("Date", myCalendar.getDateForHealthTips());
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> avObjects, AVException e) {
                    if (e == null) {
                        if (avObjects.size() > 0) {
                            String tipsPreview = avObjects.get(0).getString(NetWorkRequest.TIPS_PREVIEW);
                            if (TextUtils.isEmpty(tipsPreview)) {
                                ToastUtil.showToast(getContext(), getResources().getString(R.string.data_exception));
                                return;
                            }
                            mHandle.obtainMessage(0x78, tipsPreview).sendToTarget();
                        }
                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("WeightTipsFragment");
    }
    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("WeightTipsFragment");
    }
}
