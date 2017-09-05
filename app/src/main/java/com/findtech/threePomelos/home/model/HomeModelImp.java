package com.findtech.threePomelos.home.model;



import android.support.v4.app.Fragment;

import com.findtech.threePomelos.home.fragment.HealthTipsFragment;
import com.findtech.threePomelos.home.fragment.MusicFragment;
import com.findtech.threePomelos.home.fragment.PageFragment;
import com.findtech.threePomelos.home.fragment.UserFragment;

import java.util.ArrayList;

/**
 * Created by Alex on 2017/5/3.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/03
 *     desc    ：
 *     version ： 1.0
 */
public class HomeModelImp implements HomeModel {

    ArrayList<Fragment> fragments = new ArrayList<>();
    @Override
    public ArrayList<Fragment> getViewPagerFragments() {
        PageFragment pageFragment = new PageFragment();
        HealthTipsFragment healthTipsFragment = new HealthTipsFragment();
        MusicFragment musicFragment = new MusicFragment();
        UserFragment userFragment = new UserFragment();
        fragments.add(pageFragment);
        fragments.add(musicFragment);
        fragments.add(healthTipsFragment);
        fragments.add(userFragment);
        return fragments;
    }
}
