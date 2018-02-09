package com.findtech.threePomelos.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.PersonDataEntity;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HeightBarCharFragment extends Fragment {

    private BarChart mLineChart;
    private LinearLayout mNoDataView;
    private RelativeLayout mView;
    private String mLabel;
    private ArrayList<PersonDataEntity> heightTimeDataArray = new ArrayList<>();
    private ArrayList<CacheHeightTime> cacheHeightTimeArray = new ArrayList<>();
    private final int MAX_POINT = 7;
    private View view;
    private ProgressDialog progressDialog;
    private float value;
    private int index;
    private EditText edittext;
    private OperateDBUtils operateDBUtils;

    public HeightBarCharFragment() {
    }

    public HeightBarCharFragment(String label) {
        mLabel = label;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_height_bar_char, container, false);
        mView = (RelativeLayout) view.findViewById(R.id.id_rl_height_bar_char);
        initView(mView);
        initProgressDialog();
        operateDBUtils = new OperateDBUtils(getContext());
        return view;
    }

    private void initView(View view) {
        mLineChart = (BarChart) view.findViewById(R.id.bar_chart);
        mNoDataView = (LinearLayout) view.findViewById(R.id.no_data_view);
        int dataSize = heightTimeDataArray.size();
        if (dataSize == 0) {
            mLineChart.setVisibility(View.GONE);
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mLineChart.setVisibility(View.VISIBLE);
            mNoDataView.setVisibility(View.GONE);
        }
        BarData mBarData = getBarData();
        showChart(mLineChart, mBarData, getResources().getColor(R.color.white_00_alpha));
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("HeightBarCharFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("HeightBarCharFragment");
    }

    // 设置显示的样式
    private void showChart(BarChart barChart, BarData barData, int color) {
        barChart.setDrawBorders(false);  //是否在折线图上添加边框

        // no description text
        barChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        barChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid background
        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setGridBackgroundColor(getResources().getColor(R.color.text_pink)); // 表格的颜色，在这里是是给颜色设置一个透明度
        barChart.getXAxis().setLabelsToSkip(0);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);//隐藏X轴竖网格线
        barChart.getXAxis().setEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(getResources().getColor(R.color.white_50_alpha));
        barChart.getXAxis().setAxisLineColor(getResources().getColor(R.color.white_50_alpha));
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        // enable touch gestures
        barChart.setTouchEnabled(true); // 设置是否可以触摸

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {

                value = entry.getVal();
                index = entry.getXIndex();
            }

            @Override
            public void onNothingSelected() {
            }
        });
        barChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {
            }

            @Override
            public void onChartLongPressed(MotionEvent motionEvent) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent motionEvent) {
                if (cacheHeightTimeArray != null) {
                    if (value == 0.0f) {

                        if (cacheHeightTimeArray.get(index).getHeight() != 0.0f ||
                                (cacheHeightTimeArray.get(index).getHeight() == 0.0f &&
                                        TextUtils.isEmpty(cacheHeightTimeArray.get(index).getTime()))) {
                            return;
                        }
                    }
                    showCustomDialogAndChangeData(cacheHeightTimeArray.get(index).getTime());
                }
            }

            @Override
            public void onChartSingleTapped(MotionEvent motionEvent) {

            }

            @Override
            public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

            }

            @Override
            public void onChartScale(MotionEvent motionEvent, float v, float v1) {

            }

            @Override
            public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

            }
        });
        barChart.setExtraLeftOffset(20);//添加偏移，否则日期显示不全
        barChart.setExtraRightOffset(20);

        // enable scaling and dragging
        barChart.setDragEnabled(true);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放

        // if disabled, scaling can be done on x- and y-axis separately
        barChart.setPinchZoom(false);//
        barChart.setBackgroundColor(color);// 设置背景
        // add data
        barData.setValueTextSize(12f);
        barChart.setData(barData); // 设置数据
        // get the legend (only possible after setting data)
        Legend mLegend = barChart.getLegend(); // 设置比例图标示，就是那个一组y的value的
        // modify the legend ...
        mLegend.setEnabled(false);//隐藏table
//         mLegend.setPosition(Legend.LegendPosition.PIECHART_CENTER);
        mLegend.setForm(Legend.LegendForm.LINE);// 样式
        mLegend.setFormSize(2f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
//      mLegend.setTypeface(mTf);// 字体
//        barChart.animateX(2500); // 立即执行的动画,x轴
    }

    /**
     * 生成一个数据
     * <p/>
     * count 表示图表中有多少个坐标点
     *
     * @return
     */
    private BarData getBarData() {
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<BarEntry> yValues = new ArrayList<>();
        if (heightTimeDataArray.size() >= 0 && heightTimeDataArray.size() <= MAX_POINT) {
            for (int i = 0; i <= MAX_POINT - 1; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                if (i < heightTimeDataArray.size()) {
                    PersonDataEntity personDataEntity = heightTimeDataArray.get(i);
                    String month = Tools.getMonthFromDataPicker(personDataEntity.getTime()).replaceAll("0", "");
                    String time = month + "-" + Tools.getDayFromDataPicker(personDataEntity.getTime());
                    xValues.add(time);
                    float height = Float.parseFloat(personDataEntity.getHeight());
                    CacheHeightTime cacheHeightTime = new CacheHeightTime();
                    cacheHeightTime.setTime(personDataEntity.getTime());
                    cacheHeightTime.setHeight(height);
                    cacheHeightTimeArray.add(cacheHeightTime);
                    yValues.add(new BarEntry(height, i));
                } else {
                    xValues.add("");
                    yValues.add(new BarEntry(0.0f, i));
                    CacheHeightTime cacheHeightTime = new CacheHeightTime();
                    cacheHeightTime.setTime("");
                    cacheHeightTime.setHeight(0.0f);
                    cacheHeightTimeArray.add(cacheHeightTime);
                }
            }
        } else if (heightTimeDataArray.size() > MAX_POINT) {
            int size = heightTimeDataArray.size() - MAX_POINT;

            for (int i = size; i <= heightTimeDataArray.size() - 1; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                PersonDataEntity personDataEntity = heightTimeDataArray.get(i);
                String month = Tools.getMonthFromDataPicker(personDataEntity.getTime()).replaceAll("0", "");
                String time = month + "-" + Tools.getDayFromDataPicker(personDataEntity.getTime());
                xValues.add(time);
                float height = Float.parseFloat(personDataEntity.getHeight());
                yValues.add(new BarEntry(height, i - size));
                CacheHeightTime cacheHeightTime = new CacheHeightTime();
                cacheHeightTime.setTime(personDataEntity.getTime());
                cacheHeightTime.setHeight(height);
                cacheHeightTimeArray.add(cacheHeightTime);
            }
        }

        // create a dataset and give it a type
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, mLabel /*显示在比例图上*/);

        //用y轴的集合来设置参数
        barDataSet.setValueTextColor(getResources().getColor(R.color.white_50_alpha));
        barDataSet.setColor(getResources().getColor(R.color.white_50_alpha));// 显示颜色
        barDataSet.setHighLightColor(Color.WHITE);

        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSet); // add the datasets

        // create a data object with the datasets
        BarData barData = new BarData(xValues, barDataSets);

        return barData;
    }

    public void setTimeHeightDataArray(ArrayList<PersonDataEntity> arrayList) {
        heightTimeDataArray = arrayList;
    }

    void showCustomDialogAndChangeData(final String time) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setShowBindInfo(getResources().getString(R.string.delete_confirm));
        builder.setShowButton(true);
        builder.setCanceledOnTouchOutside(true);
        builder.setPositiveButton("更改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                showUpdateHeightDialog(time);
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.delete),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Date finalDate = Tools.getDateFromTimeStr(time);

                        final AVQuery<AVObject> query = AVQuery.getQuery(NetWorkRequest.BABY_HEIGHT);
                        query.whereEqualTo("post", AVUser.getCurrentUser());
                        query.whereEqualTo(OperateDBUtils.DATE, finalDate);
                        dialog.dismiss();
                        progressDialog.setMessage(getResources().getString(R.string.delete_message));
                        progressDialog.show();
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {

                                if (e == null) {
                                    AVObject.deleteAllInBackground(list, new DeleteCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                deleteHeight(time);
                                                progressDialog.dismiss();
                                            } else {
                                                progressDialog.dismiss();
                                                ToastUtil.showToast(getContext(), getResources().getString(R.string.delete_data_failed));
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    ToastUtil.showToast(getContext(), getResources().getString(R.string.delete_data_failed));
                                }
                            }
                        });
                    }
                });
        builder.create().show();
    }

    private int deleteHeight(String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        return getContext().getContentResolver().delete(OperateDBUtils.HEIGHT_URI, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    void showUpdateHeightDialog(final String time) {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.babay_height));
        builder.setShowEditView(true);
        builder.setShowButton(true);
        builder.setCanceledOnTouchOutside(true);

        builder.setPositiveButton(getResources().getString(R.string.btn_tagimage_show_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edittext == null || (edittext != null && TextUtils.isEmpty(edittext.getText().toString()))) {
                    return;
                }
                final String height = edittext.getText().toString();
                final Date finalDate = Tools.getDateFromTimeStr(time);
                dialog.dismiss();
                progressDialog.setMessage(getResources().getString(R.string.save_message));
                progressDialog.show();
                final AVQuery<AVObject> query = AVQuery.getQuery(NetWorkRequest.BABY_HEIGHT);
                query.whereEqualTo("post", AVUser.getCurrentUser());
                query.whereEqualTo(OperateDBUtils.DATE, finalDate);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {

                        if (e == null) {
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    AVObject avObjects = list.get(i);
                                    avObjects.put(OperateDBUtils.HEIGHT, height);
                                    avObjects.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                operateDBUtils.updateHeight(height, time);
                                            } else {
                                                ToastUtil.showToast(getContext(), getResources().getString(R.string.save_data_failed));
                                            }
                                        }
                                    });
                                }
                            }
                        } else {
                            progressDialog.dismiss();
                            ToastUtil.showToast(getContext(), getResources().getString(R.string.save_data_failed));
                        }
                    }
                });
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        customDialog = builder.create();
        edittext = (EditText) customDialog.findViewById(R.id.input_data);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext.setHint(getResources().getString(R.string.input_babay_height));
        customDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                builder.showKeyboard();
            }
        }, 200);
    }

    class CacheHeightTime {
        private String time;
        private float height;

        CacheHeightTime() {
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "time = " + time + " height = " + height;
        }
    }
}