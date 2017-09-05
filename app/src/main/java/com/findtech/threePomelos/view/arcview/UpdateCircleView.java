package com.findtech.threePomelos.view.arcview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.findtech.threePomelos.R;

/**
 * Created by gang-chen on 4/15/16.
 */
public class UpdateCircleView extends View {
    private Paint panelTextPaint;
    private Paint progressPaint;
    private int progressStroke = 6;
    private int viewHeight;
    private int viewWidth;
    private PointF centerPoint = new PointF();
    private int progress = 0;//当前进度
    private int max = 100;//最大进度
    private int bitmapWidth;

    public UpdateCircleView(Context context) {
        super(context);
    }

    public UpdateCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpdateCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.hardware_update_icon2x)).getBitmap();
        int leftX = (getWidth() - bitmapWidth) / 2;
        int leftY = 10;
        RectF rectF = new RectF(leftX, leftY, leftX + bitmapWidth, leftY + bitmapWidth);
        canvas.drawBitmap(bitmap, null, rectF, null);
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setColor(Color.WHITE);
        progressPaint.setStrokeWidth(progressStroke);
        RectF oval = new RectF(leftX, leftY, leftX + bitmapWidth, leftY + bitmapWidth);  //用于定义的圆弧的形状和大小的界限
        canvas.drawArc(oval, -90, 360 * progress / 100, false, progressPaint);
        drawUpdateText(canvas, progress);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#53d9d7"));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        bitmapWidth = viewWidth * 5 / 10;
        centerPoint.set(viewWidth / 2, viewHeight / 2);
    }

    public void setCurrentProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    private void drawUpdateText(Canvas canvas, int progress) {
        String text = getResources().getString(R.string.text_activity_firmware_update_string) + progress + "%";
        if (progress == 100) {
            text = getResources().getString(R.string.text_activity_firmware_update_finished_string);
        }
        panelTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        panelTextPaint.setStyle(Paint.Style.STROKE);
        Rect rect = new Rect();
        panelTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, getResources().getDisplayMetrics()));
        panelTextPaint.setColor(Color.WHITE);
        panelTextPaint.setFakeBoldText(false);
        panelTextPaint.getTextBounds(text, 0, text.length(), rect);
        panelTextPaint.setTextAlign(Paint.Align.CENTER);
        int textCenterX = viewWidth / 2;
        int textCenterY = viewHeight - 80;
        canvas.drawText(text, textCenterX, textCenterY, panelTextPaint);
    }
}