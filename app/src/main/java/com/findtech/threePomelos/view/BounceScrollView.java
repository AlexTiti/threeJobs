package com.findtech.threePomelos.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.findtech.threePomelos.music.utils.L;

/**
 * 支持上下反弹效果的ScrollView
 *
 */
public class BounceScrollView extends ScrollView
{

	private boolean isCalled ;

	private Callback mCallback;

	/**
	 * 包含的View
	 */
	private View mView;
	/**
	 * 存储正常时的位置
	 */
	private Rect mRect = new Rect();

	/**
	 * y坐标
	 */
	private int y;

	private boolean isFirst = true;

	public BounceScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	private ScrollingListenering scrollingListenering;
	private int top;
	private LoadListenering loadListenering;
	/***
	 * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
	 * 方法，也应该调用父类的方法，使该方法得以执行.
	 */
	@Override
	protected void onFinishInflate()
	{
		if (getChildCount() > 0)
			mView = getChildAt(0);
		if (mView != null)
		top = mView.getPaddingTop();
		super.onFinishInflate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		if (mView != null)
		{
			commonOnTouch(ev);
		}

		return super.onTouchEvent(ev);
	}

	private void commonOnTouch(MotionEvent ev)
	{
		int action = ev.getAction();
		int cy = (int) ev.getY();
		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			break;
		/**
		 * 跟随手指移动
		 */
		case MotionEvent.ACTION_MOVE:

			int dy = cy - y;
			if (isFirst)
			{
				dy = 0;
				isFirst = false;
			}
			y = cy;

			if (isNeedMove())
			{
				if (mRect.isEmpty())
				{
					/**
					 * 记录移动前的位置
					 */
					mRect.set(mView.getLeft(), mView.getTop(),
							mView.getRight(), mView.getBottom());
				}

				mView.layout(mView.getLeft(), mView.getTop() + 2 * dy / 3,
						mView.getRight(), mView.getBottom() + 2 * dy / 3);


			}

			break;
		/**
		 * 反弹回去
		 */
		case MotionEvent.ACTION_UP:
			if (!mRect.isEmpty())
			{
				resetPosition();
			}
			break;

		}
	}


	private void resetPosition()
	{

		int offset = mView.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		// 0是顶部，后面那个是底部

		if ( scrollY >= offset)
		{

			loadListenering.loadData();
		}

		Animation animation = new TranslateAnimation(0, 0, mView.getTop(),
				mRect.top);
		animation.setDuration(200);
		animation.setFillAfter(true);
		mView.startAnimation(animation);
		mView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
		mRect.setEmpty();
		isFirst = true;
		isCalled = false ; 
	}

	/***
	 * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
	 * 
	 * getHeight()：获取的是屏幕的高度
	 * 
	 * @return
	 */
	public boolean isNeedMove()
	{
		int offset = mView.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		// 0是顶部，后面那个是底部
		if (scrollY == 0 || scrollY == offset)
		{
			return true;
		}
		return false;
	}

	public void setCallBack(Callback callback)
	{
		mCallback = callback;
	}

	interface Callback
	{
		void callback();
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		scrollingListenering.scroll(t-oldt,t,top);
	}
	public interface ScrollingListenering{
		void scroll(int dt, int t, int top);
	}

	public void setScrLis(ScrollingListenering scrLis){
		this.scrollingListenering = scrLis;
	}

	public interface LoadListenering{
	void loadData();
}

	public void setLoadListenering(LoadListenering loadListenering) {
		this.loadListenering = loadListenering;
	}
}
