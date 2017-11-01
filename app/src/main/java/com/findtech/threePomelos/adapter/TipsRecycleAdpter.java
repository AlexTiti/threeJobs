package com.findtech.threePomelos.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.entity.Datebean;
import com.findtech.threePomelos.music.model.ItemClickListtener;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/20
 *   desc     :
 *   version  :   V 1.0.5
 */
public class TipsRecycleAdpter extends RecyclerView.Adapter<TipsRecycleAdpter.ViewHolder>{

   private ArrayList<String> arrayList ;
    private ItemClick itemClickListtener;
    private int posi ;
    private int selest_position = -1;

    public void setSelest_position(int selest_position) {
        this.selest_position = selest_position;
    }

    private Context context;

    public void setPosi(int posi) {
        this.posi = posi;
    }

    public void setArrayList(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    public void setItemClickListtener(ItemClick itemClickListtener,Context context) {
        this.itemClickListtener = itemClickListtener;
        this.context = context;
    }

    @Override
    public TipsRecycleAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TipsRecycleAdpter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tips_recycle_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final TipsRecycleAdpter.ViewHolder holder, final int position) {
        String s[] = arrayList.get(position).split("-");

        if (s[0] .endsWith("0")) {
            holder.textView_top.setText(s[1]+"月");
            holder.textView_below.setText(s[2]+"周");
        }else{
            holder.textView_top.setText(s[0]+"岁");
            holder.textView_below.setText(s[1]+"月");
        }

        if (position == posi) {
            holder.linearLayout.setBackgroundResource(R.color.white);
            holder.textView_top.setTextColor(context.getResources().getColor(R.color.text_pink));
            holder.textView_below.setTextColor(context.getResources().getColor(R.color.text_pink));
        }else if (selest_position == position){
            holder.linearLayout.setBackgroundResource(R.drawable.button_bac);
            holder.textView_top.setTextColor(context.getResources().getColor(R.color.text_pink));
            holder.textView_below.setTextColor(context.getResources().getColor(R.color.text_pink));
        }else{
            holder.linearLayout.setBackgroundResource(R.color.white);
            holder.textView_top.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.textView_below.setTextColor(context.getResources().getColor(R.color.text_color));
        }
          holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListtener.onClick(position,holder);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

   public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView_top;
        public TextView textView_below;
       public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            textView_top = (TextView) itemView.findViewById(R.id.text_date_top);
            textView_below = (TextView) itemView.findViewById(R.id.text_date_below);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
        }
    }

   public interface  ItemClick{
      void  onClick(int position,TipsRecycleAdpter.ViewHolder holder);
    }
}
