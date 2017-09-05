package com.findtech.threePomelos.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.MusicPlayer;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Administrator
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/15
 *   desc     :
 *   version  :   V 1.0.5
 */
public class ShowMusicAdapter extends RecyclerView.Adapter<ShowMusicAdapter.ShowMusicViewHolder> {

    private ArrayList<MusicInfo> musicInfos;
    private ItemClickListtener itemCliclListener;
    private LongClickListener longClickListener;
    public ShowMusicAdapter() {

    }
    public void setMusicInfos(ArrayList<MusicInfo> musicInfos) {
        this.musicInfos = musicInfos;
    }
    public void setItemCliclListener(ItemClickListtener itemCliclListener ) {
        this.itemCliclListener = itemCliclListener;

    }

    public void setLongClickListener(LongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }



    @Override
    public ShowMusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ShowMusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ShowMusicViewHolder holder, final int position) {
        holder.image_down.setVisibility(View.INVISIBLE);
        MusicInfo mode = musicInfos.get(position);
        holder.text_name.setText(musicInfos.get(position).musicName);
        holder.text_number.setText(String.valueOf(position+1));
        holder.text_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCliclListener.click(position);
            }
        });
        holder.text_name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickListener.longClick(position);
                return true;
            }
        });
        L.e("=============",mode.musicName+"======="+mode.songId+"====="+mode.islocal+"==="+mode.lrc);
        if (MusicPlayer.getCurrentAudioId() == mode.songId) {
            holder.text_number.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(R.drawable.song_play_icon);
        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.text_number.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public int getItemCount() {
        return musicInfos == null ? 0 : musicInfos.size();
    }



    class ShowMusicViewHolder extends RecyclerView.ViewHolder {
        private TextView text_number;
        private TextView text_name;
        private ImageView imageView;
        private ImageView image_down;

        public ShowMusicViewHolder(View itemView) {
            super(itemView);
            text_name = (TextView) itemView.findViewById(R.id.text_name);
            text_number = (TextView) itemView.findViewById(R.id.text_number);
            imageView = (ImageView) itemView.findViewById(R.id.image_sound);
            image_down = (ImageView) itemView.findViewById(R.id.image_down);
        }
    }

    public interface LongClickListener{

       void longClick(int position);

    }
}
