package com.findtech.threePomelos.view.arcview;

import java.io.Serializable;

/**
 * Created by zhi-zhang on 16/2/8.
 */
public class UserInfoModel implements Serializable {


    private double userTotal;//用户当前数据
    private String assess;//评价
    private double totalMin;//区间最小值
    private double totalMax;//区间最大值
    private String fourText;//

    public String getFourText() {
        return fourText;
    }

    public void setFourText(String fourText) {
        this.fourText = fourText;
    }

    public double getUserTotal() {
        return userTotal;
    }

    public void setUserTotal(double userTotal) {
        this.userTotal = userTotal;
    }

    public String getAssess() {
        return assess;
    }

    public void setAssess(String assess) {
        this.assess = assess;
    }

    public double getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(double totalMin) {
        this.totalMin = totalMin;
    }

    public double getTotalMax() {
        return totalMax;
    }

    public void setTotalMax(double totalMax) {
        this.totalMax = totalMax;
    }
}
