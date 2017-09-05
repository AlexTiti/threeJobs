package com.findtech.threePomelos.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/07/04
 *   desc     :
 *   version  :   V 1.0.5
 */
public class GuidePagerAdapter extends PagerAdapter {
    private List<ImageView> imageList;
    private Context context;
    private int[] imageId;

    public GuidePagerAdapter(List<ImageView> imageList, Context context, int[] imageId) {
        this.imageList = imageList;
        this.context = context;
        this.imageId = imageId;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = imageList.get(position);

        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).fromResource().load(imageId[position]).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
