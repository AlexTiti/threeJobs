package com.findtech.threePomelos.view.arcview;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.activity.InputWeightActivity;

import java.text.DecimalFormat;

/**
 * Created by zhi-zhang on 16/2/8.
 */
public class WeightArcView extends View {
    private Paint panelPaint;
    private Paint panelTextPaint;
    private Paint progressPaint;
    //进度条范围
    private RectF progressRectF;
    //刻度范围
    private RectF panelRectF;
    private int topHeight;
    private int viewHeight;
    private int viewWidth;
    private int oldViewHeight;
    private int oldViewWidth;
    private int progressRaduis;
    private int progressStroke = 4;
    private int panelStroke = 28;
    private int startAngle = 130;
    private int sweepAngle = 280;
    private PointF centerPoint = new PointF();
    private UserInfoModel dataModel;
    private double progressSweepAngle = 1;
    private double progressTotalSweepAngle;
    private ValueAnimator progressAnimator;
    private String weight;
    private Region region;
    private double oldProgressAngle = 1;
    private double oldTotalMin = 0.0;
    private boolean isShareView = false;

    public WeightArcView(Context context) {
        this(context, null);
    }

    public WeightArcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeightArcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        panelPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        panelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        if (panelPaint != null) {
            panelPaint.setColor(getResources().getColor(R.color.white_50_alpha));
            panelPaint.setStyle(Paint.Style.STROKE);
        }
        if (panelTextPaint != null) {
            panelTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        if (progressPaint != null) {
            progressPaint.setStyle(Paint.Style.STROKE);
            progressPaint.setColor(Color.WHITE);
            progressPaint.setStrokeWidth(progressStroke);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (dataModel != null) {
            viewWidth = w;
            viewHeight = h;
            oldViewWidth = oldw;
            oldViewHeight = oldh;
            progressRaduis = (w / 2) * 9 / 20;
            topHeight = (w / 2) * 3 / 10;
            centerPoint.set(viewWidth / 2, viewHeight / 2);
            weight = String.valueOf(dataModel.getTotalMin());

            Panel startPanel = new Panel();
            startPanel.setStartSweepAngle(oldProgressAngle);
            startPanel.setStartSweepValue(dataModel.getTotalMin());
            progressTotalSweepAngle = computeProgressAngle();

            Panel endPanel = new Panel();
            endPanel.setEndSweepAngle(progressTotalSweepAngle);
            endPanel.setEndSweepValue(dataModel.getUserTotal());

            progressAnimator = ValueAnimator.ofObject(new creditEvaluator(), startPanel, endPanel);
            progressAnimator.setDuration(1000);
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Panel panel = (Panel) animation.getAnimatedValue();
                    //更新进度值
                    progressSweepAngle = panel.getSesameSweepAngle();
                    DecimalFormat df = new DecimalFormat("0.0");
                    weight = String.valueOf(df.format(panel.getSesameSweepValue()));
                    invalidateView();
                }
            });

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressAnimator.start();
                    oldProgressAngle = progressTotalSweepAngle;
                }
            }, 1000);
        }
    }

    private class creditEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Panel resultPanel = new Panel();
            Panel startPanel = (Panel) startValue;
            Panel endPanel = (Panel) endValue;
            //开始扫描角度,从1度开始扫描
            double startSweepAngle = startPanel.getStartSweepAngle();
            //结束扫描的角度,为计算出来的用户体重在仪表盘上扫描过的角度
            double endSweepAngle = endPanel.getEndSweepAngle();
            double sesameSweepAngle = startSweepAngle + fraction * (endSweepAngle - startSweepAngle);
            //计算出来进度条变化时变化的角度
            resultPanel.setSesameSweepAngle(sesameSweepAngle);
            //开始扫描的值,为起始刻度0
            float startSweepValue = (float) startPanel.getStartSweepValue();
            //结束扫描的值,为用户的体重
            double endSweepValue = endPanel.getEndSweepValue();
            //计算出进度条在变化的时候体重的值
            float sesameSweepValue = startSweepValue + fraction * ((float) endSweepValue - startSweepValue);
            resultPanel.setSesameSweepValue((double) sesameSweepValue);
            return resultPanel;
        }
    }

    /**
     * 计算用户体重所占角度
     */
    private double computeProgressAngle() {
        double progressAngle;
        double totalMax = dataModel.getTotalMax();
        double userTotal = dataModel.getUserTotal();
        if (userTotal > totalMax) {
            progressAngle = sweepAngle;
        } else {
            progressAngle = sweepAngle / totalMax * userTotal + 0.1;
        }
        return progressAngle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataModel != null) {
            drawBackground(canvas);
            drawPanel(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(getResources().getColor(R.color.white_00_alpha));
    }

    /**
     * 绘制仪表盘
     */
    private void drawPanel(Canvas canvas) {
        panelPaint.setStrokeWidth(panelStroke);
        progressPaint.setStrokeWidth(panelStroke);
        progressRectF = new RectF(centerPoint.x - progressRaduis, centerPoint.y - progressRaduis, centerPoint.x + progressRaduis, centerPoint.y + progressRaduis);
        canvas.drawArc(progressRectF, startAngle, sweepAngle, false, panelPaint);
        canvas.drawArc(progressRectF, startAngle, (float) progressSweepAngle, false, progressPaint);

        panelPaint.setStrokeWidth(progressStroke);
        int panelRadius = progressRaduis * 12 / 10;
        panelRectF = new RectF(centerPoint.x - panelRadius, centerPoint.y - panelRadius, centerPoint.x + panelRadius, centerPoint.y + panelRadius);
        canvas.drawArc(panelRectF, startAngle, sweepAngle, false, panelPaint);
        canvas.save();
        canvas.rotate(-110, centerPoint.x, centerPoint.y);
        canvas.restore();
        drawPanelText(canvas);
    }

    /**
     * 绘制仪表盘文本
     */
    private void drawPanelText(Canvas canvas) {
        float drawTextY, textSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        Rect rect = new Rect();

        String text;
        drawTextY = centerPoint.y - panelRectF.height() / 2 * 0.55f;

        text = weight;
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(true);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        drawTextY = drawTextY + rect.height() + textSpace * 3;
        canvas.drawText(text, centerPoint.x - rect.width() * 3 / 4, drawTextY, panelTextPaint);

        text = "kg";
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(true);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, centerPoint.x + rect.width() * 3 / 2, drawTextY, panelTextPaint);
/*
        text = dataModel.getAssess();
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(false);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        drawTextY = drawTextY + rect.height() + textSpace;
        canvas.drawText(text, centerPoint.x - rect.width() / 2, drawTextY, panelTextPaint);
*/
        text = dataModel.getFourText();
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(false);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        drawTextY = drawTextY + rect.height() + textSpace;
        canvas.drawText(text, centerPoint.x - rect.width() / 2, drawTextY, panelTextPaint);

        if (!isShareView) {
            Drawable drawable = getResources().getDrawable(R.mipmap.weight_add_btn_nor);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bm = bd.getBitmap();
            drawTextY = drawTextY + textSpace * 2.0f;
            float addWeightLeft = centerPoint.x - bm.getWidth() / 2;
            float addWeightTop = drawTextY;
            canvas.drawBitmap(bm, centerPoint.x - bm.getWidth() / 2, drawTextY, panelTextPaint);
            region = new Region((int) addWeightLeft, (int) addWeightTop,
                    (int) (addWeightLeft + bm.getWidth()), (int) (addWeightTop + bm.getHeight()));
            text = getResources().getString(R.string.add_weight);
            panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
            panelTextPaint.setColor(Color.WHITE);
            panelTextPaint.setFakeBoldText(false);
            panelTextPaint.getTextBounds(text, 0, text.length(), rect);
            drawTextY = drawTextY + bm.getHeight() + textSpace * 1.5f;
            canvas.drawText(text, centerPoint.x - rect.width() / 2, drawTextY, panelTextPaint);
        }
    }

    public void setAsShareView(boolean isShareView) {
        this.isShareView = isShareView;
        invalidateView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                if (region.contains((int) event.getX(), (int) event.getY())) {
                    getContext().startActivity(new Intent(getContext(), InputWeightActivity.class));
                }
                break;
        }
        return true;
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setDataModel(UserInfoModel dataModel) {
        this.dataModel = dataModel;
        onSizeChanged(viewWidth, viewHeight, oldViewWidth, oldViewHeight);
        invalidateView();
    }

}
