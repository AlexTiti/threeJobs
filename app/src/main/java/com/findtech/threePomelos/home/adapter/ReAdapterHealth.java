package com.findtech.threePomelos.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.utils.IContent;

/**
 * Created by Alex on 2017/5/5.
 * <pre>
 *     author  ： Alex
 *     e-mail  ： 18238818283@sina.cn
 *     time    ： 2017/05/05
 *     desc    ：
 *     version ： 1.0
 */
public class ReAdapterHealth extends RecyclerView.Adapter<ReAdapterHealth.ReViewHolder> {
    private Context context;

    public ReAdapterHealth(Context context) {
        this.context = context;
    }

    @Override
    public ReViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.health_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReViewHolder holder, int position) {
        holder.title.setText(context.getResources().getString(IContent.healthMessIds[position]));
        holder.imageView.setImageResource(IContent.healthImageIds[position]);

    }

    @Override
    public int getItemCount() {
        return IContent.healthImageIds.length;
    }




    class ReViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView content;
        TextView  content_com;
        ImageView imageView;
        public ReViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.ima);
            title = (TextView) itemView.findViewById(R.id.text_title);
            content = (TextView) itemView.findViewById(R.id.text_content);
            content_com = (TextView) itemView.findViewById(R.id.text_2);

        }
    }
}
