package com.findtech.threePomelos.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;

import java.util.ArrayList;
import java.util.List;

public class TagImageView extends RelativeLayout {
    private Context context;

    List<View> tagViewList;

    public TagImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public TagImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public TagImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void addTextTag(final String content, int x, int y) {
        if (tagViewList == null)
            tagViewList = new ArrayList<View>();
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.tag, null);
        TextView text = (TextView) view.findViewById(R.id.tag_text);
        final RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.tag_layout);
        text.setText(content);
        layout.setOnTouchListener(new OnTouchListener() {
            int startx = 0;
            int starty = 0;
            int startx1 = 0;
            int starty1 = 0;
            int endx = 0;
            int endy = 0;
            long downTime = 0;
            long upTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // TODO Auto-generated method stub
                if (v.getId() == R.id.tag_layout) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startx = (int) event.getRawX();
                            starty = (int) event.getRawY();
                            startx1 = (int) event.getRawX();
                            starty1 = (int) event.getRawY();
                            downTime = event.getDownTime();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int x = (int) event.getRawX();
                            int y = (int) event.getRawY();
                            int dx = x - startx;
                            int dy = y - starty;
                            setPosition(v, dx, dy);
                            startx = (int) event.getRawX();
                            starty = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            upTime = event.getEventTime();
                            endx = (int) event.getRawX();
                            endy = (int) event.getRawY();
                            double mDistance = Math.sqrt(Math.abs(startx1 - endx) * (startx1 - endx) + Math.abs(starty1 - endy) * (starty - endy));
                            long mTimeDistance = upTime - downTime;
                            if (mTimeDistance > 500 && mDistance < 5) {
                                showEditTagViewDialog(layout);
                            } else if (mDistance < 5) {
                                new AlertDialog.Builder(context)
                                        .setMessage(getResources().getString(R.string.tag_delete_dialog_text))
                                        .setPositiveButton(getResources().getString(R.string.tag_delete_dialog_ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                removeTextTag(tagViewList.indexOf(layout));
                                                tagViewList.remove(layout);
                                            }
                                        })
                                        .setNegativeButton(getResources().getString(R.string.tag_delete_dialog_cancel), null)
                                        .show();
                            }
                    }
                }
                return true;
            }
        });
        this.addView(layout);
        setPosition(layout, x, y);
        tagViewList.add(layout);
    }

    public void removeTextTag(int index) {
        RelativeLayout layout = null;
        for (int i = 0; i < tagViewList.size(); i++) {
            layout = (RelativeLayout) tagViewList.get(i);
            if (i == index) {
                this.removeView(layout);
            }
        }
    }

    private void setPosition(View v, int dx, int dy) {

        int parentWidth = this.getWidth();
        int parentHeight = this.getHeight();
        int l = v.getLeft() + dx;
        int t = v.getTop() + dy;
        if (l < 0)
            l = 0;
        else if ((l + v.getWidth()) >= parentWidth) {
            l = parentWidth - v.getWidth();
        }
        if (t < 0)
            t = 0;
        else if ((t + v.getHeight()) >= parentHeight) {
            t = parentHeight - v.getHeight();
        }
        int r = l + v.getWidth();
        int b = t + v.getHeight();
        v.layout(l, t, r, b);
        LayoutParams params = (LayoutParams) v.getLayoutParams();
        params.leftMargin = l;
        params.topMargin = t;
        v.setLayoutParams(params);
    }

    private void showEditTagViewDialog(RelativeLayout layout) {
        final TextView tv = (TextView) layout.findViewById(R.id.tag_text);
        final String oringnalText = tv.getText().toString();
        final EditText textTag = new EditText(context);
        textTag.setText(oringnalText);
        new AlertDialog.Builder(context)
                .setTitle(getResources().getString(R.string.text_tag_input_dalog_title))
                .setView(textTag).setPositiveButton(getResources().getString(R.string.tag_edit_dialog_ok_button_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = textTag.getText().toString();
                if (!TextUtils.isEmpty(str)) {
                    tv.setText(str);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.tag_edit_empty_toast_txt), Toast.LENGTH_SHORT).show();
                }
            }
        })
                .setNegativeButton(getResources().getString(R.string.tag_edit_dialog_cancel_button_txt), null)
                .show();
        textTag.setFocusable(true);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}