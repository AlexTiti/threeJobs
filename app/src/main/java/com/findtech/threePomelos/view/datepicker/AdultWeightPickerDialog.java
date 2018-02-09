package com.findtech.threePomelos.view.datepicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.view.datepicker.wheel.widget.adapters.AbstractWheelTextAdapter;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.OnWheelChangedListener;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.OnWheelScrollListener;
import com.findtech.threePomelos.view.datepicker.wheel.widget.views.WheelView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/23
 *   desc     :
 *   version  :   V 1.0.5
 */
public class AdultWeightPickerDialog extends Dialog  {

    private Context context;
    private WheelView weight_wheelView;
    private WeightTextAdapter weightTextAdapter;
    private ArrayList<String> weight_list = new ArrayList<>();
    private int maxTextSize = 24;
    private int midleTextSize = 18;
    private int minTextSize = 14;
    private String weight = "55";
    private OnWeightListener onWeightListener;
    private Button confirm_button_dialog;


    public AdultWeightPickerDialog(Context context) {
        super(context, R.style.MyDialogStyleBottom);
        this.context = context;
    }

    public void setOnWeightListener(OnWeightListener onWeightListener) {
        this.onWeightListener = onWeightListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_weight_dialog);
        weight_wheelView = (WheelView) findViewById(R.id.blank_wheight);
        confirm_button_dialog= (Button) findViewById(R.id.confirm_button_dialog);
        confirm_button_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWeightListener.onClick(weight);
            }
        });
        initList();
        weightTextAdapter = new WeightTextAdapter(context,weight_list,15,maxTextSize, midleTextSize, minTextSize);
        weight_wheelView.setVisibleItems(5);
        weight_wheelView.setViewAdapter(weightTextAdapter);
        weight_wheelView.setCurrentItem(15);
        weight_wheelView.setCyclic(true);
        weight_wheelView.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                weight = (String) weightTextAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(weight, weightTextAdapter);
            }
        });
        weight_wheelView.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) weightTextAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, weightTextAdapter);
            }
        });
    }
    public void setTextviewSize(String curriteItemText, WeightTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = getNumbers(textvew.getText().toString());
            int curriteItemInt = Integer.parseInt(curriteItemText);
            int currentInt = Integer.parseInt(currentText);

            if (curriteItemInt == currentInt) {
                textvew.setTextColor(context.getResources().getColor(R.color.text_pink));
                textvew.setTextSize(maxTextSize);
            } else if (Math.abs(currentInt - curriteItemInt) == 1 || Math.abs(currentInt - curriteItemInt) + 1 == adapter.getItemsCount()) {
                textvew.setTextColor(context.getResources().getColor(R.color.divider_color));
                textvew.setTextSize(18);
            } else {
                textvew.setTextSize(minTextSize);
                textvew.setTextColor(context.getResources().getColor(R.color.hint_text));
            }
        }
    }
    public String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    private void initList(){
        for (int i = 40; i<= 150;i++){
            weight_list.add(String.valueOf(i));
        }
    }

    private class WeightTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected WeightTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int midlesize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, midlesize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }
    public interface OnWeightListener {
        public void onClick(String weight);
    }



}
