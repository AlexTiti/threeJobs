package com.findtech.threePomelos.mydevices.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.base.MyApplication;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.mydevices.bean.BluetoothLinkBean;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.RequestUtils;

import java.util.ArrayList;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/26
 *   desc     :
 *   version  :   V 1.0.5
 */
public class BlueSearchAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    final static int LAST_ITEM = 0;
    final static int ITEM = 1;
    private ArrayList<BluetoothDevice> arrayList ;
    private final static String TAG = "BlueSearchAdapter";
    private ItemClickListtener itemCliclListener;
    private Context context;
    boolean isLinking = false;
    private ArrayList<String> linking_array  = new ArrayList<>();

    public BlueSearchAdapter(Context context) {
        this.context = context;
    }

    public void setItemCliclListener(ItemClickListtener itemCliclListener) {
        this.itemCliclListener = itemCliclListener;
    }

    public void setArrayList(ArrayList<BluetoothDevice> arrayList) {
        this.arrayList = arrayList;
    }

    public void setLinking(boolean linking) {
        isLinking = linking;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       // if (viewType == ITEM)
            return new ReViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_link_item, parent, false));
//        else{
          //  return  new LastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false));
//        }

    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position ==0 || position == arrayList.size()+1)
//        return  LAST_ITEM ;
//        else
//            return ITEM;
//
//    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ReViewHolder ) {
        BluetoothDevice device = arrayList.get(position);
        String name = device.getName();
            if (isLinking)
            ((ReViewHolder) holder).text_position.setText(String.valueOf(position+2 ));
            else
                ((ReViewHolder) holder).text_position.setText(String.valueOf(position+1 ));
            ((ReViewHolder) holder).text_name.setText(name);

            if ( device.getAddress().equals(IContent.getInstacne().address) ) {
                ((ReViewHolder) holder).text_detail.setVisibility(View.VISIBLE);
                ((ReViewHolder) holder).text_state.setVisibility(View.GONE);
            }
            else {
                ((ReViewHolder) holder).text_detail.setVisibility(View.GONE);
                ((ReViewHolder) holder).text_state.setVisibility(View.VISIBLE);
            }
        }

//        else if (holder instanceof LastViewHolder){
//            if (position == 0){
//                ((LastViewHolder)holder).title_blue_search.setVisibility(View.VISIBLE);
//                ((LastViewHolder)holder).text_notice.setVisibility(View.GONE);
//            }else {
//                ((LastViewHolder)holder).title_blue_search.setVisibility(View.GONE);
//                ((LastViewHolder)holder).text_notice.setVisibility(View.VISIBLE);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0:arrayList.size();
    }

    class ReViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text_position;
        TextView text_name;
        TextView  text_state;
        TextView text_detail ;
        public ReViewHolder(View itemView) {
            super(itemView);
            text_position = (TextView) itemView.findViewById(R.id.text_position);
            text_name = (TextView) itemView.findViewById(R.id.text_name);
            text_state = (TextView) itemView.findViewById(R.id.text_state);
            text_detail = (TextView) itemView.findViewById(R.id.text_detail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            itemCliclListener.click(position);
        }
    }
    class  LastViewHolder extends RecyclerView.ViewHolder{
        private TextView text_notice;
        private TextView title_blue_search;
        public LastViewHolder(View itemView) {
            super(itemView);
            text_notice = (TextView) itemView.findViewById(R.id.text_notice);
            title_blue_search = (TextView) itemView.findViewById(R.id.title_blue_search);

        }
    }



}
