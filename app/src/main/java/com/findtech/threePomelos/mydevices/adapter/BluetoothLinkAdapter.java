package com.findtech.threePomelos.mydevices.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.home.musicbean.DeviceCarBean;
import com.findtech.threePomelos.music.model.ItemClickListtener;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.utils.IContent;

import java.util.ArrayList;


/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/05/25
 *   desc     :
 *   version  :   V 1.0.5
 */
public class BluetoothLinkAdapter extends BaseAdapter {

    private ArrayList<DeviceCarBean> arrayList ;
    private ItemClickListtener itemClickListtener;
    LongClick longClick;
    private Context context;
    private String number_0 = "0";
    private String number_1 = "1";




    public BluetoothLinkAdapter(Context context) {
        this.context = context;
    }

    public void setItemClickListtener(ItemClickListtener itemClickListtener) {
        this.itemClickListtener = itemClickListtener;

    }

    public void  setLongClick( LongClick longClick){
        this.longClick = longClick;
    }

    public void setArrayList(ArrayList<DeviceCarBean> arrayList) {
        this.arrayList = arrayList;
        L.e("==============","==========="+arrayList.size());
    }

    @Override
    public int getCount() {
        return arrayList == null ? 0:arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_device,parent,false);
            viewHolder.text_name = (TextView) convertView.findViewById(R.id.text_name_bluetooth);
            viewHolder.text_number = (TextView) convertView.findViewById(R.id.text_state_bluetooth);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_cartype);
            viewHolder.relayout_pagement = (RelativeLayout) convertView.findViewById(R.id.relayout_pagement);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        DeviceCarBean device = arrayList.get(position);
        String name = device.getDeviceName();
        viewHolder.text_name.setText(name);
        String address = device.getDeviceaAddress();
        if (address!= null && address.equals(IContent.getInstacne().address)) {
            viewHolder.text_number.setText(context.getResources().getString(R.string.device_linking));
            viewHolder.text_number.setTextColor(context.getResources().getColor(R.color.text_green));
        }else{
            viewHolder.text_number.setText(context.getResources().getString(R.string.device_nolink));
            viewHolder.text_number.setTextColor(context.getResources().getColor(R.color.divider_color));
        }

        if(number_0.equals(device.getFunctionType()) ) {
            viewHolder.imageView.setImageResource(R.drawable.face_page_high);
        }
        if (number_1.equals(device.getFunctionType())) {
            viewHolder.imageView.setImageResource(R.drawable.face_page);
        }

        viewHolder.relayout_pagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListtener.click(position);
            }
        });
        viewHolder.relayout_pagement.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick.longClick(position);
                return false;
            }
        });

        return convertView;
    }

    class ViewHolder{
        private TextView text_number;
        private TextView text_name;
        private ImageView imageView;
        private RelativeLayout relayout_pagement;
    }
    public  interface  LongClick{
void longClick(int position);
    }

}

