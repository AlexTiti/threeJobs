package com.findtech.threePomelos.view.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.view.datepicker.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.OnWheelChangedListener;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.OnWheelScrollListener;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tao on 2016/5/8.
 */
public class NativePickerDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private int columns = 1;

    private WheelView wvBlankLeft;
    private WheelView wvBlankRight;

    private WheelView wvProvince;
    private WheelView wvCity;
    private WheelView wvCounty;

    private View vChangeNative;
    private View vChangeNativeChild;
    private TextView btnSure;
    private TextView btnCancel;

    private List<String> arry_provinces = new ArrayList<String>();
    private List<String> arry_cities = new ArrayList<String>();
    private List<String> arry_counties = new ArrayList<String>();
    private List<String> arry_blank = new ArrayList<String>();

    //private String[] provinceDatas;
    private Map<String, List<String>> cityDatasMap = new HashMap<>();
    private Map<String, List<String>> countyDatasMap = new HashMap<>();

    private NativeTextAdapter mProvinceAdapter;
    private NativeTextAdapter mCityAdapter;
    private NativeTextAdapter mCountyAdapter;
    private NativeTextAdapter mBlankAdapter;

    private String currentProvince;
    private String currentCity;
    private String currentCounty;

    private int maxTextSize = 22;
    private int midleTextSize = 18;
    private int minTextSize = 16;

    private boolean issetdata = false;

    private String selectProvince;
    private String selectCity;
    private String selectCounty;

    private OnNativeListener onNativeListener;

    private JSONObject mJsonObj;


    public NativePickerDialog(Context context, int columns) {
        super(context, R.style.DatepickerDialog);
        this.context = context;
        this.columns = columns;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_native_chooser);

        wvBlankLeft = (WheelView) findViewById(R.id.blank0);
        wvBlankRight = (WheelView) findViewById(R.id.blank1);

        wvProvince = (WheelView) findViewById(R.id.wv_province);
        wvCity = (WheelView) findViewById(R.id.wv_city);
        wvCounty = (WheelView) findViewById(R.id.wv_county);

        vChangeNative = findViewById(R.id.ly_myinfo_changenative);
        vChangeNativeChild = findViewById(R.id.ly_myinfo_changenative_child);
        btnSure = (Button) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (Button) findViewById(R.id.btn_myinfo_cancel);

        vChangeNative.setOnClickListener(this);
        vChangeNativeChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initJsonData();

        initData();

        initBlank();
        mBlankAdapter = new NativeTextAdapter(context, arry_blank, 0, maxTextSize, midleTextSize, minTextSize);
        wvBlankLeft.setVisibleItems(5);
        wvBlankLeft.setViewAdapter(mBlankAdapter);
        wvBlankRight.setVisibleItems(5);
        wvBlankRight.setViewAdapter(mBlankAdapter);

        updateProvince();

        updateCity();

        updateCounty();
    }

    private void initJsonData() {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            InputStream inputStream = context.getAssets().open("ChineseCities.json");
            int length = -1;
            byte[] buff = new byte[1024];
            while ((length = inputStream.read(buff)) != -1) {
                stringBuffer.append(new String(buff, 0, length, "GBK"));
            }
            inputStream.close();
            mJsonObj = new JSONObject(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        selectProvince = "--";
        selectCity = "--";
        selectCounty = "--";
        currentProvince = "--";
        currentCity = "--";
        currentCounty = "--";

        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonProvince = jsonArray.getJSONObject(i);
                String province = jsonProvince.getString("name");
                arry_provinces.add(province);

                JSONArray jsonCityArray = null;
                try {
                    jsonCityArray = jsonProvince.getJSONArray("city");
                } catch (Exception e) {
                    continue;
                }
                List<String> cities = new ArrayList<>();
                for (int j = 0; j < jsonCityArray.length(); j++) {
                    JSONObject jsonCity = jsonCityArray.getJSONObject(j);
                    String city = jsonCity.getString("name");
                    cities.add(city);

                    JSONArray jsonCountyArray = null;
                    try {
                        jsonCountyArray = jsonCity.getJSONArray("area");
                    } catch (Exception e) {
                        continue;
                    }
                    List<String> counties = new ArrayList<>();
                    for (int k = 0; k < jsonCountyArray.length(); k++) {
                        String county = jsonCountyArray.getString(k);
                        counties.add(county);
                    }
                    countyDatasMap.put(city, counties);
                }
                cityDatasMap.put(province, cities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;

        currentProvince = arry_provinces.get(0);
        arry_cities = cityDatasMap.get(currentProvince);
        if (arry_cities != null) {
            currentCity = arry_cities.get(0);
            arry_counties = countyDatasMap.get(currentCity);
            if (arry_counties != null) {
                currentCounty = arry_counties.get(0);
            }
        }
        selectProvince = currentProvince;
        selectCity = currentCity;
        selectCounty = currentCounty;
    }

    private void initBlank() {
        for (int i = 5; i > 0; i--) {
            arry_blank.add(" " + "");
        }
    }

    private void updateProvince() {
        if (columns >= 1) {
            mProvinceAdapter = new NativeTextAdapter(context, arry_provinces, 0, maxTextSize, midleTextSize, minTextSize);
            mProvinceAdapter.setLabel("");
            wvProvince.setVisibleItems(5);
            wvProvince.setViewAdapter(mProvinceAdapter);
            wvProvince.setCurrentItem(0);
            wvProvince.setCyclic(true);
            wvProvince.setVisibility(View.VISIBLE);
            wvProvince.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (wheel == wvProvince) {
                        String currentText = (String)mProvinceAdapter.getItemText(wheel.getCurrentItem());
                        currentProvince = currentText;
                        selectProvince = currentText;
                        int currentIndex = wheel.getCurrentItem();
                        setTextviewSize(currentIndex, arry_provinces, mProvinceAdapter);
                        updateCity();

                        updateCounty();
                    }
                }
            });
            wvProvince.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {

                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    if (wheel == wvProvince) {
                        //String currentText = (String)mProvinceAdapter.getItemText(wheel.getCurrentItem());
                        int currentIndex = wheel.getCurrentItem();
                        setTextviewSize(currentIndex, arry_provinces, mProvinceAdapter);
                        //updateCity();
                    }
                }
            });
        } else {
            wvProvince.setVisibility(View.GONE);
        }
    }

    private void updateCity() {
        if (columns >= 2) {
            arry_cities = cityDatasMap.get(currentProvince);
            if (arry_cities == null) {
                arry_cities = new ArrayList<>();
                arry_cities.add("--");
            }
            currentCity = arry_cities.get(0);
            selectCity = currentCity;
            mCityAdapter = new NativeTextAdapter(context, arry_cities, 0, maxTextSize, midleTextSize, minTextSize);
            mCityAdapter.setLabel("");
            wvCity.setVisibleItems(3);
            wvCity.setViewAdapter(mCityAdapter);
            wvCity.setCurrentItem(0);
            wvCity.setCyclic(true);
            wvCity.setVisibility(View.VISIBLE);
            wvCity.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (wheel == wvCity) {
                        String currentText = (String) mCityAdapter.getItemText(wheel.getCurrentItem());
                        currentCity = currentText;
                        selectCity = currentText;
                        int currentIndex = wheel.getCurrentItem();
                        setTextviewSize(currentIndex, arry_cities, mCityAdapter);
                        updateCounty();
                    }
                }
            });
            wvCity.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {

                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    if (wheel == wvCity) {
                        //String currentText = (String) mCityAdapter.getItemText(wheel.getCurrentItem());
                        int currentIndex = wheel.getCurrentItem();
                        setTextviewSize(currentIndex, arry_cities, mCityAdapter);
                        //updateCounty();
                    }
                }
            });
        } else {
            wvCity.setVisibility(View.GONE);
        }
    }

    private void updateCounty() {
        if (columns >= 3) {
            arry_counties = countyDatasMap.get(currentCity);
            if (arry_counties == null) {
                arry_counties = new ArrayList<>();
                arry_counties.add("--");
            }
            currentCounty = arry_counties.get(0);
            selectCounty = currentCounty;
            mCountyAdapter = new NativeTextAdapter(context, arry_counties, 0, maxTextSize, midleTextSize, minTextSize);
            mCountyAdapter.setLabel("");
            wvCounty.setVisibleItems(5);
            wvCounty.setViewAdapter(mCountyAdapter);
            wvCounty.setCurrentItem(0);
            wvCounty.setCyclic(true);
            wvCounty.setVisibility(View.VISIBLE);
            wvCounty.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (wheel == wvCounty) {
                        String currentText = (String) mCountyAdapter.getItemText(wheel.getCurrentItem());
                        currentCounty = currentText;
                        selectCounty = currentText;
                        int currentIndex = wheel.getCurrentItem();
                        setTextviewSize(currentIndex, arry_counties, mCountyAdapter);
                    }
                }
            });
            wvCounty.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {

                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    if (wheel == wvCounty) {
                        //String currentText = (String) mCityAdapter.getItemText(wheel.getCurrentItem());
                        int currentIndex = wheel.getCurrentItem();
                        setTextviewSize(currentIndex, arry_counties, mCountyAdapter);
                    }
                }
            });
        } else {
            wvCounty.setVisibility(View.GONE);
        }
    }

    private void setTextviewSize(int currentIndex, List<String> nativeArrayList, NativeTextAdapter adapter) {
        List<View> viewArrayList = adapter.getTestViews();
        int viewArraySize = viewArrayList.size();
        int nativeArraySize = nativeArrayList.size();
        String currentText = nativeArrayList.get(currentIndex);
        String beforeText, afterText;

        if (currentIndex == 0) {
            if (nativeArraySize > 1) {
                beforeText = nativeArrayList.get(nativeArraySize - 1);
                afterText = nativeArrayList.get(currentIndex + 1);
            } else {
                beforeText = nativeArrayList.get(0);
                afterText = nativeArrayList.get(0);
            }
        } else if (currentIndex == nativeArraySize - 1) {
            beforeText = nativeArrayList.get(currentIndex - 1);
            afterText = nativeArrayList.get(0);
        } else {
            beforeText = nativeArrayList.get(currentIndex - 1);
            afterText = nativeArrayList.get(currentIndex + 1);
        }

        /*
        if (currentIndex == 0) {
            beforeText = "";
            if (nativeArraySize >1) {
                afterText = nativeArrayList.get(currentIndex + 1);
            } else {
                afterText = "";
            }
        } else if (currentIndex == nativeArraySize - 1) {
            if (nativeArraySize >1) {
                beforeText = nativeArrayList.get(currentIndex - 1);
            } else {
                beforeText = "";
            }
            afterText = "";
        } else {
            beforeText = nativeArrayList.get(currentIndex - 1);
            afterText = nativeArrayList.get(currentIndex + 1);
        }
        */

        for (int i = 0; i < viewArraySize; i++) {
            TextView textView = (TextView)viewArrayList.get(i);
            String textViewText = textView.getText().toString();
            if (textViewText.equals(currentText)) {
                textView.setTextColor(context.getResources().getColor(R.color.max_text));
                textView.setTextSize(maxTextSize);
            } else if (textViewText.equals(beforeText) || textViewText.equals(afterText)) {
                textView.setTextColor(context.getResources().getColor(R.color.midle_text));
                textView.setTextSize(midleTextSize);
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.min_text));
                textView.setTextSize(minTextSize);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_myinfo_sure :
                if (onNativeListener != null) {

                    onNativeListener.onClick(selectProvince, selectCity, selectCounty);
                }
            case R.id.btn_myinfo_cancel :

            default:

            dismiss();
        }
    }

    public void setNativeListener(OnNativeListener onNativeListener) {
        this.onNativeListener = onNativeListener;
    }

    public interface OnNativeListener {
        public void onClick(String province, String city, String county);
    }

    private class NativeTextAdapter extends AbstractWheelTextAdapter {
        List<String> list;

        protected NativeTextAdapter(Context context, List<String> list, int currentItem, int maxsize, int midlesize, int minsize) {
            super(context, R.layout.item_native, NO_RESOURCE, currentItem, maxsize, midlesize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    public static void setWindowSize(Dialog dialog) {
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        // 为获取屏幕宽、高
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        p.width = dm.widthPixels;
        p.alpha = 1.0f; // 设置本身透明度
        p.dimAmount = 0.6f; // 设置黑暗度
        p.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(p);
    }

}
