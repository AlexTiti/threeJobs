package com.findtech.threePomelos.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.ScreenUtils;


public class DoubleWaveView extends View {

    // y = A*sin(wx+b)+h

    private static  int WAVE_PAINT_COLOR ;
    private static int WAVE_PAINT_COLOR_TOW ;
    private static  int STRETCH_FACTOR_A;//震幅（控制波浪高度）
    private static  int OFFSET_Y = 0;//正弦函数的Y偏移量

    private static  int WaveHeight = 0;//波浪高度（Y轴方向）
    private static  int TRANSLATE_X_SPEED_ONE ;    // 第一条水波的移动速度 单位dp
    private static  int TRANSLATE_X_SPEED_TWO ;    // 第二条水波的移动速度 单位dp
    private int mXOffsetSpeedOne;//单位 PX
    private int mXOffsetSpeedTwo;//单位 PX

    private float mCycleFactorW;//这里是指代表ω周期因素 最小正周期 T = 2π/|ω|
    private int mTotalWidth, mTotalHeight;//View 控件的宽高
    private float[] mYPositions;//原始波浪

    private int mXOneOffset;//第一条波浪的X轴 偏移量
    private int mXTwoOffset;//第二条波浪的X轴 偏移量

    private boolean isAnim = true; //控制是否停止平移动画,默认开启
    private Paint mWavePaint;//画笔
    private Paint mVavePaintTwo;//第二画笔
    private DrawFilter mDrawFilter;//过滤器 消除锯齿
    private int radius;

    public DoubleWaveView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DoubleWaveView(Context context)
    {
        this(context, null);
    }

    public DoubleWaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //获取自定义属性值
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DoubleWaveView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);

            if (attr == R.styleable.DoubleWaveView_peakValue) {//振幅默认是30dp
                STRETCH_FACTOR_A = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DoubleWaveView_waveColor) {
                WAVE_PAINT_COLOR = a.getColor(attr, 0xc75564);//默认是白色

            } else if (attr == R.styleable.DoubleWaveView_waveColorTow) {
                WAVE_PAINT_COLOR_TOW = a.getColor(attr, 0xFFFFFF);//默认是粉色

            } else if (attr == R.styleable.DoubleWaveView_speedOne) {
                TRANSLATE_X_SPEED_ONE = a.getInteger(attr, 7);//默认是7

            } else if (attr == R.styleable.DoubleWaveView_speedTwo) {
                TRANSLATE_X_SPEED_TWO = a.getInteger(attr, 5);//默认是5

            } else if (attr == R.styleable.DoubleWaveView_waveHeight) {
                WaveHeight = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));//默认100dp

            }
        }
        a.recycle();

        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        mXOffsetSpeedOne = ScreenUtils.dip2px(context, TRANSLATE_X_SPEED_ONE);
        mXOffsetSpeedTwo = ScreenUtils.dip2px(context, TRANSLATE_X_SPEED_TWO);

        // 初始绘制波纹的画笔
        mWavePaint = new Paint();
       // mVavePaintTwo = new Paint();
        // 去除画笔锯齿
        mWavePaint.setAntiAlias(true);
        //mVavePaintTwo.setAntiAlias(true);
        // 设置风格为实线
        mWavePaint.setStyle(Paint.Style.FILL);
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setDrawFilter(mDrawFilter);
        for(int i=0,j=0,k=0;i<mTotalWidth;i++){
           int mHeight = (int) Math.round( Math.sqrt(Math.pow(radius,2)  - Math.pow((i-radius),2)));
            mWavePaint.setColor(WAVE_PAINT_COLOR);
            if(i+mXOneOffset<mTotalWidth){//第二条波纹图形绘制,沿着Y轴方向绘制到底
                canvas.drawLine(i,mTotalHeight-mYPositions[mXOneOffset+i]-WaveHeight,i,mTotalHeight-WaveHeight+mHeight,mWavePaint);
            }else {//大于周期值，则设置为j(与相位相关，已移动的X距离，最大值为一个周期，即控件的宽度)
                canvas.drawLine(i,mTotalHeight-mYPositions[j]-WaveHeight,i,mTotalHeight-WaveHeight+mHeight,mWavePaint);
                j++;
            }
            mWavePaint.setColor(WAVE_PAINT_COLOR_TOW);

            if(i+mXTwoOffset<mTotalWidth){//第一条波纹图形绘制
                canvas.drawLine(i,mTotalHeight-mYPositions[mXTwoOffset+i]-WaveHeight,i,mTotalHeight-WaveHeight+mHeight,mWavePaint);
            }else {//大于周期值，则设置为k(与相位相关，已移动的X距离)
                canvas.drawLine(i,mTotalHeight-mYPositions[k]-WaveHeight,i,mTotalHeight-WaveHeight+mHeight,mWavePaint);
                k++;
            }
        }

        // 改变两条波纹的移动点
        mXOneOffset += mXOffsetSpeedOne*0.45;
        mXTwoOffset += mXOffsetSpeedTwo*0.45;

        // 如果已经移动到结尾处，则重头记录
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset > mTotalWidth) {
            mXTwoOffset = 0;
        }

        if (isAnim){
            new Thread(mRunnable).start();
        }
    }

    public void setAnim(Boolean isAnim){//是否启动动画
        if (!this.isAnim==isAnim){
            this.isAnim = isAnim;
            postInvalidate();
        }
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            {
                try {
                    //界面更新的频率,每20ms更新一次界面
                    Thread.sleep(20);
                    //通知系统更新界面,相当于调用了onDraw函数
                    postInvalidate();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下控件设置的宽高
        mTotalWidth = w;
        mTotalHeight = h;
        radius = Math.min(mTotalHeight,mTotalWidth)/2 ;
          // 一维数组, 用于保存原始波纹的y值
        mYPositions = new float[mTotalWidth];

        // 将Sin函数周期定为view总宽度， ω = 2π/T
        mCycleFactorW = (float) (2 * Math.PI / mTotalWidth);

        // 根据view总宽度得出所有对应的y值,即计算出正弦图形对应位置
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }
}