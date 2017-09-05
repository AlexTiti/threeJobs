package com.findtech.threePomelos.base;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.avos.avoscloud.AVAnalytics;


/**
 * Created by Alex on 2017/5/3.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */
public abstract class BaseLazyFragment extends Fragment {

    /**
     * Log tag
     */
    protected static String TAG_LOG = null;
    private boolean isFirstVisible = true;
    private boolean isPrepared;

    /**
     * context
     */
    protected Context mContext = null;
    protected MyApplication app ;
    protected final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 122;



    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        app = (MyApplication) activity.getApplication();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TAG_LOG = this.getClass().getSimpleName();





    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        if (getContentViewLayoutID() != 0) {
            View view = inflater.inflate(getContentViewLayoutID(), container,false);
            return view;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Nullable
    @Override
    public void onViewCreated( @Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewsAndEvents(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        }
    }
    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
    }


    //----------------------------------------------- 对外提供的方法----------------------------
    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents( View view);

    /**
     * when fragment is visible for the first time, here we can do some initialized work or refresh data only once
     */
    protected abstract void onFirstUserVisible();

    /**
     * this method like the fragment's lifecycle method onResume()
     */
    protected abstract void onUserVisible();





}
