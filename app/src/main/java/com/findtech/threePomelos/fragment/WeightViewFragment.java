package com.findtech.threePomelos.fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.arcview.UserInfoModel;
import com.findtech.threePomelos.view.arcview.WeightArcView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeightViewFragment extends Fragment implements RequestUtils.UserWeightChangeListener {


    private ArrayList<PersonDataEntity> timeWeightDataArray = new ArrayList<>();
    private WeightBarCharFragment weightBarCharFragment;
    private final Object object = new Object();
    private WeightArcView mWeightArcView;
    private LinearLayout linearLayout;
    private double oldTotalMin = 0.0;
    private double newTotalMin = 0.0;

    private SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeWeightDataArray = MyApplication.getInstance().getUserWeightData();
        getContext().getContentResolver().registerContentObserver(OperateDBUtils.HEIGHT_URI, true, contentObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weightinfo, container, false);
        mWeightArcView = ((WeightArcView) view.findViewById(R.id.weight_panel));
        mWeightArcView.setDataModel(getData());
        weightBarCharFragment = new WeightBarCharFragment();
        weightBarCharFragment.setTimeWeightDataArray(timeWeightDataArray);
        getFragmentManager().beginTransaction().replace(R.id.curve_fragment, weightBarCharFragment).commitAllowingStateLoss();
        RequestUtils requestUtils = new RequestUtils(getContext());
        requestUtils.setUserWeightChangeListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("WeightViewFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().getContentResolver().unregisterContentObserver(contentObserver);
    }

    public void setAsShareFragement(boolean isShare) {
        if (mWeightArcView != null) {
            mWeightArcView.setAsShareView(isShare);
        }
        if (isShare) {
            view.setBackgroundColor(getResources().getColor(R.color.text_pink));
        } else {
            view.setBackgroundColor(getResources().getColor(R.color.text_pink));
        }
    }

    private UserInfoModel getData() {
        UserInfoModel model = new UserInfoModel();
        String weightNum = RequestUtils.getSharepreference(getContext()).getString(RequestUtils.WEIGHT, "0");

        if (Tools.isNumericOrDot(weightNum)) {
            newTotalMin = Double.parseDouble(weightNum);
            model.setUserTotal(newTotalMin);
        } else {
            model.setUserTotal(0);
        }
        model.setAssess("体重良好");
        model.setTotalMin(oldTotalMin);

        oldTotalMin = newTotalMin;
        model.setTotalMax(40);
        String weightState = RequestUtils.getSharepreference(getContext()).getString(RequestUtils.WEIGHT_HEALTH_STATE, "0~0");
        model.setFourText(getContext().getResources().getString(R.string.xliff_standard_weight_num, weightState));
        return model;
    }


    @Override
    public void userWeightChange() {
        queryUserWeightData();
    }

    private void queryUserWeightData() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String columns[] = new String[]{OperateDBUtils.TIME, OperateDBUtils.USER_ID, OperateDBUtils.WEIGHT};
                        String selection = OperateDBUtils.USER_ID + "=?";
                        cursor = getContext().getContentResolver().query(OperateDBUtils.WEIGHT_URI, columns, selection,
                                new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                        timeWeightDataArray.clear();
                        Tools.SortArrayList sort = new Tools.SortArrayList();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String timePoint = cursor.getString(cursor.getColumnIndex(OperateDBUtils.TIME));
                                String weightNum = cursor.getString(cursor.getColumnIndex(OperateDBUtils.WEIGHT));
                                timePoint = Tools.getYearFromDataPicker(timePoint) + "-" +
                                        Tools.getMonthFromDataPicker(timePoint) + "-" +
                                        Tools.getDayFromDataPicker(timePoint);
                                PersonDataEntity personDataEntity = new PersonDataEntity();
                                personDataEntity.setTime(timePoint);
                                personDataEntity.setWeight(weightNum);
                                timeWeightDataArray.add(personDataEntity);

                            }
                            Collections.sort(timeWeightDataArray, sort);
                        }
                        if (timeWeightDataArray.size() > 0) {
                            RequestUtils.getSharepreferenceEditor(getContext()).putString(RequestUtils.WEIGHT,
                                    timeWeightDataArray.get(timeWeightDataArray.size() - 1).getWeight()).commit();
                        } else {
                            RequestUtils.getSharepreferenceEditor(getContext()).putString(RequestUtils.WEIGHT,
                                    "0").commit();
                        }
                        handler.sendEmptyMessage(0x01);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        });
    }


    ContentObserver contentObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.getPath().contains(OperateDBUtils.WEIGHT)) {
                queryUserWeightData();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                mWeightArcView.setDataModel(getData());
                mWeightArcView.invalidate();
                WeightBarCharFragment newWeightBarCharFragment = new WeightBarCharFragment("");
                newWeightBarCharFragment.setTimeWeightDataArray(timeWeightDataArray);

                getFragmentManager().beginTransaction().remove(weightBarCharFragment).commitAllowingStateLoss();
                getFragmentManager().beginTransaction().replace(R.id.curve_fragment, newWeightBarCharFragment).commitAllowingStateLoss();
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("WeightViewFragment");
    }
}