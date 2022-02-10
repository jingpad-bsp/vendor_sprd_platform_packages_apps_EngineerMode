package com.sprd.engineermode.debuglog;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.util.Log;
import android.graphics.Color;

import com.sprd.engineermode.R;

public class CheckItemAdapter extends BaseAdapter {

    private static final String TAG = "CheckItemAdapter";

    private ArrayList<String> mItemList;
    private Context mContext;

    public CheckItemAdapter(ArrayList<String> itemList, Context context) {
        mItemList = itemList;
        mContext = context;
        Log.d(TAG, "itemList size"+mItemList.size());
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position < mItemList.size()) {
            return mItemList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder = null;
        if (convertView == null) {
            vHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.detailed_item, null);
            vHolder.itemName = (TextView) convertView
                .findViewById(R.id.item_name);
            convertView.setTag(R.id.action_save, vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag(R.id.action_save);
            vHolder.itemName.requestLayout();
        }
        Log.d(TAG, "title:"+mItemList.get(position));
        vHolder.itemName.setText(mItemList.get(position));
        //vHolder.itemName.setTextColor(Color.BLACK);
        return convertView;
    }

    public static class ViewHolder {
        public TextView itemName;
    }
}
