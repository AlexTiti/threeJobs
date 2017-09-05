package com.findtech.threePomelos.fragment;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVUser;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.InputHeightActivity;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeightViewFragment extends Fragment implements View.OnClickListener, RequestUtils.UserHeightChangeListener {

    private ImageButton inputHeight;
    private ArrayList<PersonDataEntity> timeHeightDataArray = new ArrayList<>();
    private HeightBarCharFragment heightBarCharFragment;
    private TextView heightNum, normalHeight;
    private final Object object = new Object();
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timeHeightDataArray = MyApplication.getInstance().getUserHeightData();
        getContext().getContentResolver().registerContentObserver(OperateDBUtils.HEIGHT_URI, true, contentObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_heightinfo, container, false);
        heightBarCharFragment = new HeightBarCharFragment();
        heightBarCharFragment.setTimeHeightDataArray(timeHeightDataArray);
        getFragmentManager().beginTransaction().replace(R.id.bar_chart_layout, heightBarCharFragment).commitAllowingStateLoss();
        inputHeight = (ImageButton) view.findViewById(R.id.add_height);
        heightNum = (TextView) view.findViewById(R.id.id_tv_height);
        normalHeight = (TextView) view.findViewById(R.id.normal_height);
        String heightState = RequestUtils.getSharepreference(getContext()).getString(RequestUtils.HEIGHT_HEALTH_STATE, "0~0");
        normalHeight.setText(getResources().getString(R.string.xliff_standard_height_num, heightState));
        inputHeight.setOnClickListener(this);
        RequestUtils requestUtils = new RequestUtils(getContext());
        requestUtils.setUserHeightChangeListener(this);
        return view;
    }

    public void setAsShareFragement(boolean isShare) {
        if (isShare) {
            inputHeight.setVisibility(View.INVISIBLE);
            view.setBackgroundColor(getResources().getColor(R.color.text_pink));
        } else {
            inputHeight.setVisibility(View.VISIBLE);
            view.setBackgroundColor(getResources().getColor(R.color.text_pink));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String heightNumStr = RequestUtils.getSharepreference(getContext()).getString(RequestUtils.HEIGHT, "- -");
        heightNum.setText(heightNumStr);
        AVAnalytics.onFragmentStart("HeightViewFragment");
    }



    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("HeightViewFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Override
    public void onClick(View view) {
        if (view == inputHeight) {
            startActivity(new Intent(getContext(), InputHeightActivity.class));
        }
    }

    @Override
    public void userHeightChange() {
        queryUserHeightData();
    }

    private void queryUserHeightData() {

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    Cursor cursor = null;
                    try {
                        String columns[] = new String[]{OperateDBUtils.TIME, OperateDBUtils.USER_ID, OperateDBUtils.HEIGHT};
                        String selection = OperateDBUtils.USER_ID + "=?";
                        cursor = getContext().getContentResolver().query(OperateDBUtils.HEIGHT_URI, columns, selection,
                                new String[]{AVUser.getCurrentUser().getObjectId()}, null);
                        timeHeightDataArray.clear();
                        Tools.SortArrayList sort = new Tools.SortArrayList();
                        if (cursor != null && cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String timePoint = cursor.getString(cursor.getColumnIndex(OperateDBUtils.TIME));
                                String heightNum = cursor.getString(cursor.getColumnIndex(OperateDBUtils.HEIGHT));
                                timePoint = Tools.getYearFromDataPicker(timePoint) + "-" +
                                        Tools.getMonthFromDataPicker(timePoint) + "-" +
                                        Tools.getDayFromDataPicker(timePoint);
                                PersonDataEntity personDataEntity = new PersonDataEntity();
                                personDataEntity.setTime(timePoint);
                                personDataEntity.setHeight(heightNum);
                                timeHeightDataArray.add(personDataEntity);
//                            Log.d("ZZ", "heightNum = " + heightNum);
//                            Log.d("ZZ", "timePoint = " + timePoint);
                            }
                            Collections.sort(timeHeightDataArray, sort);
                        }
                        if (timeHeightDataArray.size() > 0) {
                            RequestUtils.getSharepreferenceEditor(getContext()).putString(RequestUtils.HEIGHT,
                                    timeHeightDataArray.get(timeHeightDataArray.size() - 1).getHeight()).commit();
                        } else {
                            RequestUtils.getSharepreferenceEditor(getContext()).putString(RequestUtils.HEIGHT,
                                    "0").commit();
                        }
                        handler.sendEmptyMessage(0x00);
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
            if (uri.getPath().contains(OperateDBUtils.HEIGHT)) {
                queryUserHeightData();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x00) {

                HeightBarCharFragment newHeightBarCharFragment = new HeightBarCharFragment();
                newHeightBarCharFragment.setTimeHeightDataArray(timeHeightDataArray);
                try {
                    String heightNumStr = RequestUtils.getSharepreference(getContext()).getString(RequestUtils.HEIGHT, "- -");
                    heightNum.setText(heightNumStr);
                } catch (Exception e) {
                    Log.e("ZZ", "HeightViewFragment handler e " + e);
                }
                getFragmentManager().beginTransaction().remove(heightBarCharFragment).commitAllowingStateLoss();
                getFragmentManager().beginTransaction().replace(R.id.bar_chart_layout, newHeightBarCharFragment).commitAllowingStateLoss();
            }
        }
    };


}