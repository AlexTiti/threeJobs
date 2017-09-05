package com.findtech.threePomelos.musicserver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ScreenUtils;
import com.findtech.threePomelos.view.DoubleWaveView;


/**
 * @author:Jack Tony
 * 
 * 重要：注意要申请权限！！！！
 *  <!-- 悬浮窗的权限 -->
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
 */
public class FloatingService extends Service {
	/**
	 * 定义浮动窗口布局
	 */
	RelativeLayout mlayout;
	/**
	 * 悬浮窗控件
	 */
	ImageView mfloatingIv;
	DoubleWaveView doubleWaveView;
	/**
	 * 悬浮窗的布局
	 */
	LayoutParams wmParams;
	LayoutInflater inflater;
	/**
	 * 创建浮动窗口设置布局参数的对象
	 */
	WindowManager mWindowManager;

	//触摸监听器
	GestureDetector mGestureDetector;
    /**
	 * 控制动画的停止或继续
     */
    private boolean isPlay = false;
    /**
	 * 屏幕的尺寸
     */
    private int screenWidth;
    private int screenHeight;
    public int viewWidth;
    public int viewHeight;
	DoubleWaveViewPlayOrPauseReceiver  receiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("onCreate","onCreate");
		initWindow();//设置窗口的参数

		IntentFilter intentFilter = new IntentFilter(IContent.ACTION_PLAY_OR_PAUSE);
		  receiver = new DoubleWaveViewPlayOrPauseReceiver();
		registerReceiver(receiver,intentFilter);
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e("onCreate","onStartCommand");
		initFloating();//设置悬浮窗图标
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mlayout != null) {	
			// 移除悬浮窗口
			mWindowManager.removeView(mlayout);
		}
		unregisterReceiver(receiver);
	}
	
	///////////////////////////////////////////////////////////////////////
	
	/**
	 * 初始化windowManager
	 */
	private void initWindow() {
		Log.e("onCreate","initWindow");
		mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        screenHeight = mWindowManager.getDefaultDisplay().getHeight();
		wmParams = getParams(wmParams);//设置好悬浮窗的参数
		// 悬浮窗默认显示以左上角为起始坐标
		wmParams.gravity = Gravity.RIGHT| Gravity.BOTTOM;
		//悬浮窗的开始位置，因为设置的是从左上角开始，所以屏幕左上角是x=0;y=0		
		wmParams.x = 100;
		wmParams.y = 300;
        viewWidth = wmParams.width;
        viewHeight = wmParams.height;
		//得到容器，通过这个inflater来获得悬浮窗控件
		inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mlayout = (RelativeLayout) inflater.inflate(R.layout.floating_layout, null);
		// 添加悬浮窗的视图
		mWindowManager.addView(mlayout, wmParams);
	}
	
	/** 对windowManager进行设置
	 * @param wmParams
	 * @return
	 */
	public LayoutParams getParams(LayoutParams wmParams){
		Log.e("onCreate","getParams");
		wmParams = new LayoutParams();
		//设置window type 下面变量2002是在屏幕区域显示，2003则可以显示在状态栏之上
		//wmParams.type = LayoutParams.TYPE_PHONE;
		wmParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
		//wmParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
		//设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
       //wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE; 
        //设置可以显示在状态栏上
        wmParams.flags =  LayoutParams.FLAG_NOT_FOCUSABLE| LayoutParams.FLAG_NOT_TOUCH_MODAL|
        LayoutParams.FLAG_LAYOUT_IN_SCREEN| LayoutParams.FLAG_LAYOUT_INSET_DECOR|
        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        
        //设置悬浮窗口长宽数据  
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;

        return wmParams;
	}
	
	/**
	 * 找到悬浮窗的图标，并且设置事件
	 * 设置悬浮窗的点击、滑动事件
	 */
	private void initFloating() {
		Log.e("onCreate","initFloating");
		//mfloatingIv = (ImageButton) mlayout.findViewById(R.id.floating_imageView);
		//mfloatingIv.getBackground().setAlpha(150);

		doubleWaveView = (DoubleWaveView) mlayout.findViewById(R.id.waveView);
		mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
		//设置监听器
		//mfloatingIv.setOnTouchListener(new FloatingListener());
		doubleWaveView.setOnTouchListener(new FloatingListener());


	}
	
	//开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
	private int mTouchStartX,mTouchStartY,mTouchCurrentX,mTouchCurrentY;
	//开始时的坐标和结束时的坐标（相对于自身控件的坐标）
	private int mStartX,mStartY,mStopX,mStopY;
	private boolean isMove;//判断悬浮窗是否移动
	

	private class FloatingListener implements OnTouchListener {

		@Override
		public boolean onTouch(View arg0, MotionEvent event) {

			int action = event.getAction();
			switch(action){ 
				case MotionEvent.ACTION_DOWN:
					isMove = false;
					mTouchStartX = (int)event.getRawX();
					mTouchStartY = (int)event.getRawY();
					mStartX = (int)event.getX();
					mStartY = (int)event.getY();
					break; 
				case MotionEvent.ACTION_MOVE:
					mTouchCurrentX = (int) event.getRawX();
					mTouchCurrentY = (int) event.getRawY();
					wmParams.x += mTouchStartX - mTouchCurrentX ;
					wmParams.y += mTouchStartY - mTouchCurrentY ;
					mWindowManager.updateViewLayout(mlayout, wmParams);
					mTouchStartX = mTouchCurrentX;
					mTouchStartY = mTouchCurrentY; 
		            break;
				case MotionEvent.ACTION_UP:
					mStopX = (int)event.getX();
					mStopY = (int)event.getY();
					//System.out.println("|X| = "+ Math.abs(mStartX - mStopX));
					//System.out.println("|Y| = "+ Math.abs(mStartY - mStopY));
					if(Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1){
                      isMove = true;
					}
                    stepAside();
		            break; 
			}
			return mGestureDetector.onTouchEvent(event);  //此处返回false，OnClickListener获取监听而FloatingListener却不执行
		}

	}

    private void stepAside() {
        float viewCenterX = wmParams.x + viewWidth/2;
        float viewCenterY = wmParams.y + viewHeight/2;
        float dxLeft = viewCenterX;
        float dyUp = viewCenterY;
        float dxRight  = screenWidth - dxLeft;
        float dyDown = screenHeight -dyUp;
        float result = getMin(dxLeft,dyUp,dxRight,dyDown);
        if (result == dxLeft){
            wmParams.x = 0;
        }else if (result == dxRight){
            wmParams.x = screenWidth-viewWidth;
        }else if (result == dyUp){
            wmParams.y = 0;
        }else{
            wmParams.y = screenHeight-viewHeight- ScreenUtils.getStatusBarHeight(this);
        }
        mWindowManager.updateViewLayout(mlayout, wmParams);

    }

    private float getMin(float dxLeft, float dyUp, float dxRight, float dyDown){
        float a = Math.min(dxLeft,dyUp);
        float b = Math.min(dxRight,dyDown);
        float c = Math.min(a,b);
        return c;
    }

	

	class MyOnGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!isMove) {
                sendBroadcast(new Intent(IContent.SINGLE_CLICK));


            }
			return super.onSingleTapConfirmed(e);
		}

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isMove) {
				sendBroadcast(new Intent(IContent.DOUBLE_CLICK));
                doubleWaveView.setAnim(isPlay);
                isPlay = !isPlay;
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
           // Toast.makeText(getApplicationContext(), "onDown",Toast.LENGTH_SHORT).show();
            return true;
        }

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			if (!isMove) {
				sendBroadcast(new Intent(IContent.DOUBLE_CLICK));
				doubleWaveView.setAnim(isPlay);
				isPlay = !isPlay;
			}
		}
	}


	class DoubleWaveViewPlayOrPauseReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action == null)
				return;

			if (action.equals(IContent.ACTION_PLAY_OR_PAUSE)){
				doubleWaveView.setAnim(intent.getBooleanExtra("isPlay",true));

			}

		}
	}






}
