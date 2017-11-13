package com.findtech.threePomelos.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.musicbean.MusicNetBean;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.MusicPlayer;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ToastUtil;


import java.util.List;

/**
 * Created by Alex on 2017/5/7.
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/07
 *     desc    ：
 *     version ： 1.0
 */
public class MusicAdapter extends BaseAdapter {

    private List<MusicInfo> avObjectList;
    private ItemClickListtener itemCliclListener;
    private downClick downClick;
    private  Animation animation;

    public void setItemCliclListener(ItemClickListtener itemCliclListener,downClick downClick) {
        this.itemCliclListener = itemCliclListener;
        this.downClick = downClick;
        initAnimation();
    }

    private void initAnimation(){
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setRepeatCount(-1);

    }

    public void setAvObjectList(List<MusicInfo> avObjectList) {
        this.avObjectList = avObjectList;
    }

    @Override
    public int getCount() {
        return avObjectList == null ? 0 : avObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        return avObjectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false);
            viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name);
            viewHolder.text_number = (TextView) convertView.findViewById(R.id.text_number);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_sound);
            viewHolder.image_down = (ImageView) convertView.findViewById(R.id.image_down);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();

        }
        MusicInfo mode = avObjectList.get(position);
        String name = avObjectList.get(position).musicName;
        viewHolder.text_name.setText(name);
        viewHolder.text_number.setText(String.valueOf(position+1));

        if ("downing".equals(mode.artist)) {
            L.e("downing",mode.musicName);
            viewHolder.image_down.setEnabled(false);
            viewHolder.image_down.setImageResource(R.drawable.iconload);
            viewHolder.image_down.setAnimation(animation);
            animation.start();
        }else if (mode.islocal) {
            L.e("downing",mode.musicName);
            if (animation != null)
                viewHolder.image_down.clearAnimation();
             viewHolder.image_down.setEnabled(false);
             viewHolder.image_down.setImageResource(R.drawable.icon_downed);
        } else {
            viewHolder.image_down.setEnabled(true);
            viewHolder.image_down.setImageResource(R.drawable.icon_down);
            if (animation != null)
                viewHolder.image_down.clearAnimation();
        }
        viewHolder.text_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCliclListener.click(position);
            }
        });

        viewHolder.image_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downClick.downclick(position);
            }
        });

        if (MusicPlayer.getCurrentAudioId() == mode.songId) {
            viewHolder.text_number.setVisibility(View.INVISIBLE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.imageView.setImageResource(R.drawable.song_play_icon);
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.text_number.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder{
        private TextView text_number;
        private TextView text_name;
        private ImageView imageView;
        private ImageView image_down;
    }

public  interface downClick{
    void downclick(int position);
}
}
