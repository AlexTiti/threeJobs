package com.findtech.threePomelos.utils;

import android.content.Context;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.utils.L;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author Administrator
 */
public class MyCalendar {
    Calendar datebegin;
    Calendar dateend;
    DateFormat df;

    private Context context;

    private String day ;
    private String month;
    private String year;
    private String week;

    public Calendar getDatebegin() {
        return datebegin;
    }

    public void setDatebegin(Calendar datebegin) {
        this.datebegin = datebegin;
    }

    public Calendar getDateend() {
        return dateend;
    }

    public void setDateend(Calendar dateend) {
        this.dateend = dateend;
    }

    public DateFormat getDf() {
        return df;
    }

    public void setDf(DateFormat df) {
        this.df = df;
    }

    public MyCalendar() {
        df = new SimpleDateFormat("yyyy-MM-dd");
        datebegin = Calendar.getInstance();
        dateend = Calendar.getInstance();

    }

    public MyCalendar(String begin, String end ,Context context) throws ParseException {
        this.context = context;
        df = new SimpleDateFormat("yyyy-MM-dd");
        datebegin = Calendar.getInstance();
        dateend = Calendar.getInstance();
        datebegin.setTime(df.parse(begin));
        dateend.setTime(df.parse(end));

        day = context.getResources().getString(R.string.label_date_d);
        month = context.getResources().getString(R.string.label_date__month);
        year = context.getResources().getString(R.string.label_date_year);
        week = context.getResources().getString(R.string.label_date_week);
    }

    /**
     * 当前日比较
     *
     * @return
     */
    private boolean compareTo() {
        return datebegin.get(Calendar.DAY_OF_MONTH) > dateend.get(Calendar.DAY_OF_MONTH);
    }

    private int CalculatorYear() {
        int year1 = datebegin.get(Calendar.YEAR);
        int year2 = dateend.get(Calendar.YEAR);
        int month1 = datebegin.get(Calendar.MONTH);
        int month2 = dateend.get(Calendar.MONTH);
        int year = year2 - year1;
        if (compareTo()) //计算天时向月借了一个月
        {
            month2 -= 1;
        }
        if (month1 > month2) {
            year -= 1;
        }
        return year;
    }

    private int CalculatorMonth() {

        int month1 = datebegin.get(Calendar.MONTH);
        int month2 = dateend.get(Calendar.MONTH);
        int month = 0;
        if (compareTo()) //计算天时向月借了一个月
            month2 -= 1;
        if (month2 >= month1)
            month = month2 - month1;
        else if (month2 < month1) // 借一整年
            month = 12 + month2 - month1;
        return month;

    }

    private int CalculatorDay() {

        int day11 = datebegin.get(Calendar.DAY_OF_MONTH);
        int day21 = dateend.get(Calendar.DAY_OF_MONTH);
        if (day21 >= day11) {
            return day21 - day11;
        } else {// 借一整月
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateend.getTime());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(dateend.DAY_OF_MONTH, -1);
            return cal.getActualMaximum(Calendar.DATE) + day21 - day11;
        }
    }

    /**
     * 返回两个时间相隔的多少年
     *
     * @return
     */
    public int getYear() {
        return CalculatorYear() > 0 ? CalculatorYear() : 0;
    }

    /**
     * 返回除去整数年后的月数
     *
     * @return
     */
    public int getMonth() {
        int month = CalculatorMonth() % 12;
        return (month > 0 ? month : 0);
    }

    /**
     * 返回除去整月后的天数
     *
     * @return
     */
    private int getDay() {
        int day = CalculatorDay();
        return day;
    }

    /**
     * 返回现个日期间相差的年月天以@分隔
     *
     * @return
     */
    public String getDate() {
        String date  ;
        if (getYear() == 0) {
            date = getMonth() + month + getDay() + day;
            if (getMonth() == 0) {
                date = getDay() + day;
            }
        } else {
            date = getYear() + year+ getMonth() + month + getDay() + day;
        }
        return date;
    }

    /**
     * @return year-month eg.1-4
     */
    public String getStandardDate() {
        return getYear() + "-" + getMonth();
    }

    /**
     * 格式:0-2-1, 意思是0年2月1周
     */
    public String getDateForHealthTips() {
        String dateFromat;
        if (getYear() == 0) {
            if (getMonth() == 11) {
                if ((getDay() / 7 + 1) > 4) {
                    dateFromat = (getYear() + 1) + "-" + "1" + "-0";
                } else {
                    dateFromat = "0-" + getMonth() + "-" + (getDay() / 7 + 1);
                }
            } else {
                if ((getDay() / 7 + 1) > 4) {
                    dateFromat = "0-" + (getMonth() + 1) + "-1";
                } else {
                    dateFromat = "0-" + getMonth() + "-" + (getDay() / 7 + 1);
                }
            }
        } else if (getYear() == 1 || getYear() == 2) {
            if (getMonth() == 0) {

                dateFromat = getYear() + "-" + (getMonth() + 1) + "-0";
                L.e("=============",dateFromat);
            } else {
                dateFromat = getYear() + "-" + getMonth() + "-0";
            }
        } else {
            dateFromat = "2-12-0";
        }
        return dateFromat;
    }

    /**
     * 格式:0年2月1周
     */
    public String getDateForHealthTipsStr() {
        String dateFromat;
        if (getYear() == 0) {
            if (getMonth() == 11) {
                if ((getDay() / 7 + 1) > 4) {
                    dateFromat = (getYear() + 1) + year + "1" + month;
                } else {
                    dateFromat = getMonth() + month + (getDay() / 7 + 1) + week;
                }
            } else {
                if ((getDay() / 7 + 1) > 4) {
                    dateFromat = (getMonth() + 1) + month + "1" + week;
                } else {
                    dateFromat = getMonth() + month + (getDay() / 7 + 1) + week;
                }
            }
        } else if (getYear() == 1 || getYear() == 2) {
            if (getMonth() == 0) {
                dateFromat = getYear() +  year + getMonth() + 1 + month;
            } else {
                dateFromat = getYear() +  year + getMonth() + month;
            }
        } else {
            dateFromat = "2"+year+"12"+month;
        }
        return dateFromat;
    }



}
