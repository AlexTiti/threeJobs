package com.findtech.threePomelos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.RequestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhi.zhang on 3/19/16.
 */
public class BabyInfoAdapter extends RecyclerView.Adapter<BabyInfoAdapter.MyViewHolder> {

    private String[] title ;
    RequestUtils.MyItemClickListener mItemClickListener;
    Context mContext;
    private Map<String, String> mListItem = new HashMap<>();
    public BabyInfoAdapter(Context context) {
        mContext = context;
    }

    public void setTitle(String[] title) {
        this.title = title;
    }

    @Override
    public BabyInfoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.baby_info_list_item, parent, false));//不添加false，会出现crash
        return holder;
    }

    @Override
    public void onBindViewHolder(BabyInfoAdapter.MyViewHolder holder, int position) {

        holder.babyInfoTitle.setText(title[position]);
        if (position == 3) {
            holder.babyInfo.setText(mContext.getString(R.string.height_info, mListItem.get(title[position])));
        }else if (position == 4) {
            holder.babyInfo.setText(mContext.getString(R.string.weight_info, mListItem.get(title[position])));
        } else {
            holder.babyInfo.setText(mListItem.get(title[position]));
        }
        if (position > 1 && position < 5) {
            holder.arrowRight.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mListItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView babyInfoTitle;
        TextView babyInfo;
        ImageView arrowRight;

        public MyViewHolder(View itemView) {
            super(itemView);
            babyInfoTitle = (TextView) itemView.findViewById(R.id.baby_info_title);
            babyInfo = (TextView) itemView.findViewById(R.id.baby_info);
            arrowRight = (ImageView) itemView.findViewById(R.id.arrow_right_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(view, getPosition());
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(RequestUtils.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setListItem(Map<String, String> listItem) {
        mListItem = listItem;
    }

}
