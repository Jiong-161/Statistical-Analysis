package com.example.accounts.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.example.accounts.R;

import java.util.ArrayList;
import java.util.List;

/*
 * 历史账单界面，展示日历的月份列表,点击日历表，弹出对话框，当中的GridView对应的适配器
 * */
public class CalendarAdapter extends BaseAdapter {
    Context context;//上下文对象，用于访问资源和其他上下文相关的功能。
    List<String>mDatas;//用于存储月份数据
    public int year;
    public int selPos = -1;//选中的月份的位置

    public void setYear(int year) {
        this.year = year;
        mDatas.clear();
        loadDatas(year);
        notifyDataSetChanged();
    }

    public CalendarAdapter(Context context, int year) {
        this.context = context;
        this.year = year;
        mDatas = new ArrayList<>();
        loadDatas(year);
    }

    private void loadDatas(int year) {
        for (int i = 1; i < 13; i++) {
            String data = year+"/"+i;
            mDatas.add(data);
        }
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_dialogcal_gv,parent,false);
        TextView tv = convertView.findViewById(R.id.item_dialogcal_gv_tv);
        tv.setText(mDatas.get(position));
        tv.setBackgroundResource(R.color.grey_f3f3f3);
        tv.setTextColor(Color.BLACK);
        if (position == selPos) {
            tv.setBackgroundResource(R.color.green_006400);
            tv.setTextColor(Color.WHITE);
        }
        return convertView;
    }
}
