package com.findtech.threePomelos.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class WeightBarCharFragment extends Fragment {

    private LineChart mLineChart;
    private RelativeLayout mView;
    private String mLabel;
    private LinearLayout mNoDataView;
    private ArrayList<PersonDataEntity> weightTimeDataArray = new ArrayList<>();
    private ArrayList<CacheWeightTime> cacheWeightTimeArray = new ArrayList<>();
    private final int MAX_POINT = 7;
    private ProgressDialog progressDialog;
    private float value;
    private int index;
    private EditText edittext;
    private OperateDBUtils operateDBUtils;

    public WeightBarCharFragment() {
    }

    public WeightBarCharFragment(String label) {
        mLabel = label;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curveinfo, container, false);
        mView = (RelativeLayout) view.findViewById(R.id.id_rl_weight_curve_chart);
        initView(mView);
        initProgressDialog();
        operateDBUtils = new OperateDBUtils(getContext());
        return view;
    }

    private void initView(View view) {
        mLineChart = (LineChart) view.findViewById(R.id.line_chart);
        mNoDataView = (LinearLayout) view.findViewById(R.id.no_data_view);
        int dataSize = weightTimeDataArray.size();
        if (dataSize == 0) {
            mLineChart.setVisibility(View.GONE);
            mNoDataView.setVisibility(View.VISIBLE);
        } else {
            mLineChart.setVisibility(View.VISIBLE);
            mNoDataView.setVisibility(View.GONE);
        }
        LineData mLineData = getLineData();
        showChart(mLineChart, mLineData, getResources().getColor(R.color.white_00_alpha));
    }

    @Override
    public void onResume() {
        super.onResume();
        AVAnalytics.onFragmentStart("WeightBarCharFragment");
    }
    @Override
    public void onPause() {
        super.onPause();
        AVAnalytics.onFragmentEnd("WeightBarCharFragment");
    }

    // 设置显示的样式
    private void showChart(LineChart lineChart, final LineData lineData, int color) {
        lineChart.setDrawBorders(false);  //是否在折线图上添加边框

        // no description text
        lineChart.setDescription("");// 数据描述
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid background
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setGridBackgroundColor(getResources().getColor(R.color.text_pink)); // 表格的颜色，在这里是是给颜色设置一个透明度
        lineChart.getXAxis().setLabelsToSkip(0);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);//隐藏X轴竖网格线
        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextColor(getResources().getColor(R.color.white_50_alpha));
        lineChart.getXAxis().setAxisLineColor(getResources().getColor(R.color.white_50_alpha));
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        // enable touch gestures
        lineChart.setTouchEnabled(true); // 设置是否可以触摸
        lineChart.setExtraLeftOffset(20);//添加偏移，否则日期显示不全
        lineChart.setExtraRightOffset(20);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight h) {
                Log.d("ZZZ", "Entry = " + entry + " dataSetIndex = " + dataSetIndex + " Highlight = " + h);
                value = entry.getVal();
                index = entry.getXIndex();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        lineChart.setOnChartGestureListener(new OnChartGestureListener() {
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
                if (cacheWeightTimeArray != null) {
                    if (value == 0.0f) {
                        if (cacheWeightTimeArray.get(index).getWeight() != 0.0f ||
                                (cacheWeightTimeArray.get(index).getWeight() == 0.0f &&
                                        TextUtils.isEmpty(cacheWeightTimeArray.get(index).getTime()))) {
                            return;
                        }
                    }
                    showCustomDialogAndChangeData(cacheWeightTimeArray.get(index).getTime());
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

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);//

        lineChart.setBackgroundColor(color);// 设置背景

        // add data
        lineData.setValueTextSize(12f);
        lineChart.setData(lineData); // 设置数据

        // get the legend (only possible after setting data)
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示，就是那个一组y的value的

        // modify the legend ...
        mLegend.setEnabled(false);//隐藏table
//         mLegend.setPosition(Legend.LegendPosition.PIECHART_CENTER);
        mLegend.setForm(Legend.LegendForm.LINE);// 样式
        mLegend.setFormSize(2f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
//      mLegend.setTypeface(mTf);// 字体
//        lineChart.animateX(2500); // 立即执行的动画,x轴
    }

    /**
     * 生成一个数据
     *
     * @return
     */
    private LineData getLineData() {
        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();

        if (weightTimeDataArray.size() >= 0 && weightTimeDataArray.size() <= MAX_POINT) {
            for (int i = 0; i <= MAX_POINT - 1; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                if (i < weightTimeDataArray.size()) {
                    PersonDataEntity personDataEntity = weightTimeDataArray.get(i);
                    String month = Tools.getMonthFromDataPicker(personDataEntity.getTime()).replaceAll("0", "");
                    String time = month + "-" + Tools.getDayFromDataPicker(personDataEntity.getTime());
                    xValues.add(time);
                    float weight = Float.parseFloat(personDataEntity.getWeight());
                    yValues.add(new BarEntry(weight, i));
                    CacheWeightTime cacheWeightTime = new CacheWeightTime();
                    cacheWeightTime.setTime(personDataEntity.getTime());
                    cacheWeightTime.setWeight(weight);
                    cacheWeightTimeArray.add(cacheWeightTime);
                }
            }
        } else if (weightTimeDataArray.size() > MAX_POINT) {
            int size = weightTimeDataArray.size() - MAX_POINT;

            for (int i = size; i <= weightTimeDataArray.size() - 1; i++) {
                // x轴显示的数据，这里默认使用数字下标显示
                PersonDataEntity personDataEntity = weightTimeDataArray.get(i);
                String month = Tools.getMonthFromDataPicker(personDataEntity.getTime()).replaceAll("0", "");
                String time = month + "-" + Tools.getDayFromDataPicker(personDataEntity.getTime());
                xValues.add(time);
                float weight = Float.parseFloat(personDataEntity.getWeight());
                yValues.add(new BarEntry(weight, i - size));
                CacheWeightTime cacheWeightTime = new CacheWeightTime();
                cacheWeightTime.setTime(personDataEntity.getTime());
                cacheWeightTime.setWeight(weight);
                cacheWeightTimeArray.add(cacheWeightTime);
            }
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet lineDataSet = new LineDataSet(yValues, mLabel /*显示在比例图上*/);
        lineDataSet.setFillAlpha(110);

        //用y轴的集合来设置参数
        lineDataSet.setLineWidth(1.75f); // 线宽
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setColor(Color.WHITE);// 显示颜色
        lineDataSet.setCircleColor(Color.WHITE);// 圆形的颜色
        lineDataSet.setValueTextColor(getResources().getColor(R.color.white_50_alpha));
//        lineDataSet.setHighLightColor(getResources().getColor(R.color.red_color)); // 高亮的线的颜色
        lineDataSet.setDrawHighlightIndicators(false);//禁掉，点击高亮线
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCubic(true);
        lineDataSet.setCubicIntensity(0.0f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(getResources().getColor(R.color.white));

        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(xValues, lineDataSets);

        return lineData;
    }

    public void setTimeWeightDataArray(ArrayList<PersonDataEntity> arrayList) {
        weightTimeDataArray = arrayList;
    }

    void showCustomDialogAndChangeData(final String time) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getActivity().getResources().getString(R.string.notice));
        builder.setShowBindInfo(getActivity().getResources().getString(R.string.delete_confirm));
        builder.setShowButton(true);
        builder.setCanceledOnTouchOutside(true);
        builder.setPositiveButton(getActivity().getResources().getString(R.string.change), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showUpdateWeightDialog(time);
            }
        });

        builder.setNegativeButton(getActivity().getResources().getString(R.string.delete),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final Date finalDate = Tools.getDateFromTimeStr(time);
                        final AVQuery<AVObject> query = AVQuery.getQuery(NetWorkRequest.BABY_WEIGHT);
                        query.whereEqualTo("post", AVUser.getCurrentUser());
                        query.whereEqualTo(OperateDBUtils.DATE, finalDate);
                        dialog.dismiss();
                        progressDialog.setMessage(getActivity().getResources().getString(R.string.delete_message));
                        progressDialog.show();
                        query.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                Log.d("ZZZ", "findInBackground = " + list.size());
                                if (e == null) {
                                    AVObject.deleteAllInBackground(list, new DeleteCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                deleteWeight(time);
                                                progressDialog.dismiss();
                                            } else {
                                                progressDialog.dismiss();
                                                ToastUtil.showToast(getContext(),getActivity().getResources().getString(R.string.delete_data_failed) );
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    ToastUtil.showToast(getContext(), getActivity().getResources().getString(R.string.delete_data_failed) );
                                }
                            }
                        });
                    }
                });
        builder.create().show();
    }

    private int deleteWeight(String time) {
        String where = OperateDBUtils.TIME + " = ? " + " AND " + OperateDBUtils.USER_ID + " = ?";
        return getContext().getContentResolver().delete(OperateDBUtils.WEIGHT_URI, where,
                new String[]{time, AVUser.getCurrentUser().getObjectId()});
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    void showUpdateWeightDialog(final String time) {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setTitle(getActivity().getResources().getString(R.string.babay_weight));
        builder.setShowEditView(true);
        builder.setShowButton(true);
        builder.setCanceledOnTouchOutside(true);

        builder.setPositiveButton(getActivity().getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (edittext == null || (edittext != null && TextUtils.isEmpty(edittext.getText().toString()))) {
                    return;
                }
                final String weight = edittext.getText().toString();
                final Date finalDate = Tools.getDateFromTimeStr(time);
                dialog.dismiss();
                progressDialog.setMessage(getActivity().getResources().getString(R.string.save_message));
                progressDialog.show();
                final AVQuery<AVObject> query = AVQuery.getQuery(NetWorkRequest.BABY_WEIGHT);
                query.whereEqualTo("post", AVUser.getCurrentUser());
                query.whereEqualTo(OperateDBUtils.DATE, finalDate);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        Log.d("ZZZ", "findInBackground = " + list.size());
                        if (e == null) {
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    AVObject avObjects = list.get(i);
                                    avObjects.put(OperateDBUtils.WEIGHT, weight);
                                    avObjects.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            progressDialog.dismiss();
                                            if (e == null) {
                                                operateDBUtils.updateWeightToDB(weight, time);
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

        builder.setNegativeButton(getActivity().getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        customDialog = builder.create();
        edittext = (EditText) customDialog.findViewById(R.id.input_data);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edittext.setHint(getActivity().getResources().getString(R.string.input_babay_weight));
        customDialog.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                builder.showKeyboard();
            }
        }, 200);
    }


    class CacheWeightTime {
        private String time;
        private float weight;

        CacheWeightTime() {
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "time = " + time + " weight = " + weight;
        }
    }

}