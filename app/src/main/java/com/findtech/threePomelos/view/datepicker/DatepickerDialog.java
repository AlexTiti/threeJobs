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
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.datepicker.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.OnWheelChangedListener;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.OnWheelScrollListener;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.WheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期选择对话框
 *
 * @author ywl
 */
public class DatepickerDialog extends Dialog implements android.view.View.OnClickListener {

    private Context context;
    private WheelView wvBlankLeft;
    private WheelView wvBlankRight;

    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;

    private View vChangeBirth;
    private View vChangeBirthChild;
    private TextView btnSure;
    private TextView btnCancel;

    private ArrayList<String> arry_years = new ArrayList<String>();
    private ArrayList<String> arry_months = new ArrayList<String>();
    private ArrayList<String> arry_days = new ArrayList<String>();
    private ArrayList<String> arry_blank = new ArrayList<String>();

    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDaydapter;

    private CalendarTextAdapter mBlankAdapter;


    private int month;
    private int day;

    private int currentYear = getYear();
    private int currentMonth = 1;
    private int currentDay = 1;

    private int maxTextSize = 22;
    private int midleTextSize = 18;
    private int minTextSize = 16;

    private boolean issetdata = false;

    private String selectYear;
    private String selectMonth;
    private String selectDay;

    private OnBirthListener onBirthListener;

    public DatepickerDialog(Context context) {
        super(context, R.style.DatepickerDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_chooser);
        wvBlankLeft = (WheelView) findViewById(R.id.blank0);
        wvBlankRight = (WheelView) findViewById(R.id.blank1);

        wvYear = (WheelView) findViewById(R.id.wv_birth_year);
        wvMonth = (WheelView) findViewById(R.id.wv_birth_month);
        wvDay = (WheelView) findViewById(R.id.wv_birth_day);

        vChangeBirth = findViewById(R.id.ly_myinfo_changebirth);
        vChangeBirthChild = findViewById(R.id.ly_myinfo_changebirth_child);
        btnSure = (Button) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (Button) findViewById(R.id.btn_myinfo_cancel);

        vChangeBirth.setOnClickListener(this);
        vChangeBirthChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        initData();

        initBlank();
        mBlankAdapter = new CalendarTextAdapter(context, arry_blank, setYear(currentYear), maxTextSize, midleTextSize, minTextSize);
        wvBlankLeft.setVisibleItems(5);
        wvBlankLeft.setViewAdapter(mBlankAdapter);
        wvBlankRight.setVisibleItems(5);
        wvBlankRight.setViewAdapter(mBlankAdapter);

        initYears();

        mYearAdapter = new CalendarTextAdapter(context, arry_years, setYear(currentYear), maxTextSize, midleTextSize, minTextSize);
        mYearAdapter.setLabel("年");
        wvYear.setVisibleItems(5);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(setYear(currentYear));
        wvYear.setCyclic(true);

        initMonths(month);
        mMonthAdapter = new CalendarTextAdapter(context, arry_months, setMonth(currentMonth), maxTextSize, midleTextSize, minTextSize);
        mMonthAdapter.setLabel("月");

        wvMonth.setVisibleItems(5);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(setMonth(currentMonth));
        wvMonth.setCyclic(true);

        initDays(day);
        mDaydapter = new CalendarTextAdapter(context, arry_days, currentDay - 1, maxTextSize, midleTextSize, minTextSize);
        mDaydapter.setLabel("日");
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDaydapter);
        wvDay.setCurrentItem(currentDay - 1);
        wvDay.setCyclic(true);

        wvYear.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                selectYear = currentText;
                setTextviewSize(currentText, mYearAdapter);
                currentYear = Integer.parseInt(currentText);
                setYear(currentYear);
                initMonths(month);
                mMonthAdapter = new CalendarTextAdapter(context, arry_months, 0, maxTextSize, midleTextSize, minTextSize);
                mMonthAdapter.setLabel("月");
                wvMonth.setVisibleItems(5);
                wvMonth.setViewAdapter(mMonthAdapter);
                wvMonth.setCurrentItem(0);
            }
        });

        wvYear.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mYearAdapter);
            }
        });

        wvMonth.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                selectMonth = currentText;
                setTextviewSize(currentText, mMonthAdapter);
                setMonth(Integer.parseInt(currentText));
                initDays(day);
                mDaydapter = new CalendarTextAdapter(context, arry_days, 0, maxTextSize, midleTextSize, minTextSize);
                mDaydapter.setLabel("日");

                wvDay.setVisibleItems(5);
                wvDay.setViewAdapter(mDaydapter);
                wvDay.setCurrentItem(0);
            }
        });

        wvMonth.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMonthAdapter);
            }
        });

        wvDay.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
                selectDay = currentText;
            }
        });

        wvDay.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) mDaydapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mDaydapter);
            }
        });

    }

    public void initBlank() {
        for (int i = 5; i > 0; i--) {
            arry_blank.add(" " + "");
        }
    }

    public void initYears() {
        for (int i = getYear(); i > 2004; i--) {
            arry_years.add(i + "");
        }
    }

    public void initMonths(int months) {
        arry_months.clear();
        for (int i = 1; i <= months; i++) {
            arry_months.add(i + "");
        }
    }

    public void initDays(int days) {
        arry_days.clear();
        for (int i = 1; i <= days; i++) {
            arry_days.add(i + "");
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int midlesize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, midlesize, minsize);
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

    public void setBirthdayListener(OnBirthListener onBirthListener) {
        this.onBirthListener = onBirthListener;
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onBirthListener != null) {
                if ((Integer.parseInt(selectYear) >= getYear()) && (Integer.parseInt(selectMonth) > getMonth() ||
                        Integer.parseInt(selectMonth) == getMonth() && Integer.parseInt(selectDay) > getDay())) {
                    ToastUtil.showToast(context, context.getResources().getString(R.string.text_datepick_show_errortime_set));
                } else {
                    String formatSelectMonth = (selectMonth.length() < 2) ? "0" + selectMonth : selectMonth;
                    String formatSelectDay = (selectDay.length() < 2) ? "0" + selectDay : selectDay;
                    onBirthListener.onClick(selectYear, formatSelectMonth, formatSelectDay);
                    dismiss();
                }
            }
        } else if (v == btnSure) {

        } else if (v == vChangeBirthChild) {
            return;
        } else {
            dismiss();
        }
    }

    public interface OnBirthListener {
        public void onClick(String year, String month, String day);
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = getNumbers(textvew.getText().toString());

            int curriteItemInt = Integer.parseInt(curriteItemText);
            int currentInt = Integer.parseInt(currentText);

            if (curriteItemInt == currentInt) {
                textvew.setTextColor(context.getResources().getColor(R.color.max_text));
                textvew.setTextSize(maxTextSize);
            } else if (Math.abs(currentInt - curriteItemInt) == 1 || Math.abs(currentInt - curriteItemInt) + 1 == adapter.getItemsCount()) {
                textvew.setTextColor(context.getResources().getColor(R.color.midle_text));
                textvew.setTextSize(20);
            } else {
                textvew.setTextSize(minTextSize);
                textvew.setTextColor(context.getResources().getColor(R.color.min_text));

            }
        }
    }

    public String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH) + 1;
    }

    public int getDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DATE);
    }

    public void initData() {
        setDate(getYear(), getMonth(), getDay());
    /*	this.currentDay = 1;
        this.currentMonth = 1;*/
    }

    /**
     * 设置年月日
     *
     * @param year
     * @param month
     * @param day
     */
    public void setDate(int year, int month, int day) {
        selectYear = year + "";
        selectMonth = month + "";
        selectDay = day + "";
        issetdata = true;
        this.currentYear = year;
        this.currentMonth = month;
        this.currentDay = day;
        if (year == getYear()) {
            this.month = getMonth();
        } else {
            this.month = 12;
        }
        calDays(year, month);
    }

    /**
     * 设置年份
     *
     * @param year
     */
    public int setYear(int year) {
        int yearIndex = 0;
        /*if (year != getYear()) {*/
        this.month = 12;
        /*} else {
            this.month = getMonth();
		}*/
        for (int i = getYear(); i > 2006; i--) {
            if (i == year) {
                return yearIndex;
            }
            yearIndex++;
        }
        return yearIndex;
    }

    /**
     * 设置月份
     *
     * @param month
     * @return
     */
    public int setMonth(int month) {
        int monthIndex = 0;
        calDays(currentYear, month);
        for (int i = 1; i < this.month; i++) {
            if (month == i) {
                return monthIndex;
            } else {
                monthIndex++;
            }
        }
        return monthIndex;
    }

    /**
     * 计算每月多少天
     *
     * @param month
     */
    public void calDays(int year, int month) {
        boolean leayyear = false;
        if (year % 4 == 0 && year % 100 != 0) {
            leayyear = true;
        } else {
            leayyear = false;
        }
        for (int i = 1; i <= 12; i++) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    this.day = 31;
                    break;
                case 2:
                    if (leayyear) {
                        this.day = 29;
                    } else {
                        this.day = 28;
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    this.day = 30;
                    break;
            }
        }
        /*if (year == getYear() && month == getMonth()) {
			this.day = getDay();
		}*/
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