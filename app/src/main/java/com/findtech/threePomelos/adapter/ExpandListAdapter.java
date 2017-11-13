package com.findtech.threePomelos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *
 *   @author   :   Alex
 *   @e_mail   :   18238818283@sina.cn
 *   @time     :   2017/11/09
 *   @desc     :
 *   @version  :   V 1.0.9
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    
    private List<ArrayList<String>> lists;
    private List<String> title_list;
    private Context context;

    public ExpandListAdapter(List<ArrayList<String>> lists,List<String> title_list,Context context) {
        this.lists = lists;
        this.title_list = title_list;
        this.context = context;
    }

    @Override
    public int getGroupCount()
    {
        return lists.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return lists.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return lists.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        GroupViewHolder groupViewHolder = null;
        if (convertView == null){

            groupViewHolder = new GroupViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_itemtext,parent,false);
            convertView.setTag(groupViewHolder);
        }else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.textView = (TextView) convertView.findViewById(R.id.textView);
        groupViewHolder.textView.setTextSize(16);
        groupViewHolder.textView.setPadding(5,5,5,5);
        groupViewHolder.textView.setTextColor(context.getResources().getColor(R.color.black));
        groupViewHolder.textView.setText(title_list.get(groupPosition));


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder = null;
        if (convertView == null){

            childViewHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expand_itemtext,parent,false);
            convertView.setTag(childViewHolder);
        }else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.childTextView = (TextView) convertView.findViewById(R.id.textView);

        childViewHolder.childTextView.setText(lists.get(groupPosition).get(childPosition));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder{
        TextView textView;

    }
    static class ChildViewHolder{
        TextView childTextView;
    }
}
