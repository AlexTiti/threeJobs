package com.findtech.threePomelos.entity;

import android.text.TextUtils;

/**
 * Created by lenovo on 2016/4/30.
 */
public class TravelInfoEntity {

    private String todayMileage;
    private String totalMileage;
    private String averageSpeed;
    private String totalCalor;
    private String todayCalor;
    private String adultWeight;

    private String text_tempurature;
    private String electric_rate;


    public String getElectric_rate() {
        if (TextUtils.isEmpty(electric_rate)) {
            return "0";
        } else {
            return electric_rate;
        }
    }

    public void setElectric_rate(String electric_rate) {
        this.electric_rate = electric_rate;
    }

    public String getText_tempurature() {
        if (TextUtils.isEmpty(text_tempurature)) {
            return "0";
        } else {
            return text_tempurature;
        }
    }

    public void setText_tempurature(String text_tempurature) {
        this.text_tempurature = text_tempurature;
    }

    public String getTotalCalor() {
        if (TextUtils.isEmpty(totalCalor)) {
            return "0.0";
        } else {
            return totalCalor;
        }
    }

    public void setTotalCalor(String totalCalor) {
        if (TextUtils.isEmpty(totalCalor)) {
            this.totalCalor = "0.0";
        } else {
            this.totalCalor = totalCalor;
        }

    }

    public String getTodayCalor() {
        if (TextUtils.isEmpty(todayCalor)) {
            return "0.0";
        } else {
            return todayCalor;
        }

    }

    public void setTodayCalor(String todayCalor) {
        if (TextUtils.isEmpty(todayCalor)) {
            this.todayCalor = "0.0";
        } else {
            this.todayCalor = todayCalor;
        }

    }

    public String getAdultWeight() {
        if (TextUtils.isEmpty(adultWeight)) {
            return "0.0";
        } else {
            return adultWeight;
        }

    }

    public void setAdultWeight(String adultWeight) {
        if (TextUtils.isEmpty(adultWeight)) {
            this.todayMileage = "0.0";
        } else {
            this.adultWeight = adultWeight;
        }

    }

    private static TravelInfoEntity travelInfoEntity;

    public TravelInfoEntity() {
    }

    public static TravelInfoEntity getInstance() {
        if (travelInfoEntity == null) {
            travelInfoEntity = new TravelInfoEntity();
        }
        return travelInfoEntity;
    }

    public String getTodayMileage() {
        if (TextUtils.isEmpty(todayMileage)) {
            return "0.0";
        } else {
            return todayMileage;
        }
    }

    public void setTodayMileage(String todayMileage) {
        if (TextUtils.isEmpty(todayMileage)) {
            this.todayMileage = "0.0";
        } else {
            this.todayMileage = todayMileage;
        }
    }

    public String getTotalMileage() {
        if (TextUtils.isEmpty(totalMileage)) {
            return "0.0";
        } else {
            return totalMileage;
        }
    }

    public void setTotalMileage(String totalMileage) {
        this.totalMileage = totalMileage;
    }

    public String getAverageSpeed() {
        if (TextUtils.isEmpty(averageSpeed)) {
            return "0.0";
        } else {
            return averageSpeed;
        }
    }

    public void setAverageSpeed(String averageSpeed) {
        if (TextUtils.isEmpty(todayMileage)) {
            this.averageSpeed = "0.0";
        } else {
            this.averageSpeed = averageSpeed;
        }
    }
}
