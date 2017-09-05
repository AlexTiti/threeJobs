package com.findtech.threePomelos.view.arcview;

/**
 * Created by zhi-zhang on 16/2/8.
 */
public class Panel {
    private double startSweepAngle;//扫描开始角度
    private double endSweepAngle;//扫描结束角度
    private double startSweepValue;//扫描开始过程对应的值
    private double endSweepValue;//扫描结束过程对应的值
    private double sesameSweepAngle;//计算出来的扫描中的角度
    private double sesameSweepValue;//计算出来的扫描中的角度对应的体重值

    public double getStartSweepAngle() {
        return startSweepAngle;
    }

    public void setStartSweepAngle(double startSweepAngle) {
        this.startSweepAngle = startSweepAngle;
    }

    public double getEndSweepAngle() {
        return endSweepAngle;
    }

    public void setEndSweepAngle(double endSweepAngle) {
        this.endSweepAngle = endSweepAngle;
    }

    public double getStartSweepValue() {
        return startSweepValue;
    }

    public void setStartSweepValue(double startSweepValue) {
        this.startSweepValue = startSweepValue;
    }

    public double getEndSweepValue() {
        return endSweepValue;
    }

    public void setEndSweepValue(double endSweepValue) {
        this.endSweepValue = endSweepValue;
    }

    public double getSesameSweepAngle() {
        return sesameSweepAngle;
    }

    public void setSesameSweepAngle(double sesameSweepAngle) {
        this.sesameSweepAngle = sesameSweepAngle;
    }

    public double getSesameSweepValue() {
        return sesameSweepValue;
    }

    public void setSesameSweepValue(double sesameSweepValue) {
        this.sesameSweepValue = sesameSweepValue;
    }
}
