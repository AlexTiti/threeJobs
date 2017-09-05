package com.findtech.threePomelos.home;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.IContent;

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
public class FragmentAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments ;
    Context context;

    public FragmentAdapter(FragmentManager fm, Context context,ArrayList<Fragment> fragments ) {
        super(fm);
        this.context =context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0:fragments.size();
    }

//    public void setFragments(ArrayList<Fragment> fragments ){
//        this.fragments = fragments;
//    }

    public View getView(int position){
        View view = LayoutInflater.from(context).inflate(R.layout.tabitem,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_tab);
        imageView.setBackgroundResource(IContent.tabImageIds[position]);
        TextView textView = (TextView) view.findViewById(R.id.text_tab);

        textView.setText(IContent.tabtextIds[position]);
        return  view;
    }
}
