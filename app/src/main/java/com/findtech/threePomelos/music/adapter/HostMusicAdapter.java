package com.findtech.threePomelos.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/26
 *   desc     :
 *   version  :   V 1.0.5
 */
public class HostMusicAdapter  extends BaseAdapter{
    private ArrayList<MusicInfo> musicInfos ;
    private Context mContext;
    private CollectClick collectClick;

    public HostMusicAdapter(Context mContext,CollectClick collectClick) {
        this.mContext = mContext;
        this.collectClick = collectClick;
    }

    private ItemClickListtener itemCliclListener;


    public void setItemCliclListener(ItemClickListtener itemCliclListener) {
        this.itemCliclListener = itemCliclListener;
    }

    public void setMusicInfos(ArrayList<MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
    }


    @Override
    public int getCount() {
        return musicInfos == null ? 0 : musicInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
       final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_item, parent, false);
            viewHolder.icon_number = (TextView) convertView.findViewById(R.id.img_icon_host);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_host_item);
            viewHolder.text_name_host = (TextView) convertView.findViewById(R.id.text_name_host);
            viewHolder.text_category = (TextView) convertView.findViewById(R.id.text_title);
            viewHolder.img_like = (ImageView) convertView.findViewById(R.id.img_like);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MusicInfo musicInfo = musicInfos.get(position);
        if (position == 0){
            viewHolder.icon_number.setBackgroundResource(R.drawable.icon_first_host);
            viewHolder.icon_number.setText( null);
        }else if (position == 1){
            viewHolder.icon_number.setBackgroundResource(R.drawable.icon_sec_host);
            viewHolder.icon_number.setText(null);
        }else if (position == 2){
            viewHolder.icon_number.setBackgroundResource(R.drawable.icon_three_host);
            viewHolder.icon_number.setText( null);
        }else{
            viewHolder.icon_number.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            viewHolder.icon_number.setText( String.valueOf(position+1 ));
        }
        viewHolder.img_like.setImageResource(R.drawable.icon_like_host);
        if (IContent.getInstacne().collectedList.contains(musicInfo.musicName)){
            viewHolder.isCollect = true;
              viewHolder.img_like.setImageResource(R.drawable.icon_like_host_selected);
        }

        if (!viewHolder.isCollect){
            viewHolder.img_like.setImageResource(R.drawable.icon_like_host);
        }
        viewHolder.img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( viewHolder.isCollect){
                    collectClick.deleteCollect(position);

                }else{
                    collectClick.collect(position);
                }

            }
        });

        Glide.with(mContext).load(musicInfo.faceImage).error(R.drawable.icon_rhyme).into(viewHolder.imageView);
        viewHolder.text_name_host.setText(musicInfo.musicName);
        viewHolder.text_category.setText( musicInfo.typeName );

        return convertView;
    }

    class  ViewHolder {

        private TextView icon_number;
        private ImageView imageView;
        private TextView text_name_host;
        private TextView text_category;
        private ImageView img_like;
        private boolean isCollect = false;

    }

    public interface CollectClick{
        void collect(int position);
        void deleteCollect(int position);

    }

}
