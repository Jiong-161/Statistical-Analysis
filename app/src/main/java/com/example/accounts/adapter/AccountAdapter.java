package com.example.accounts.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.accounts.R;
import com.example.accounts.db.AccountBean;
import com.example.accounts.utils.MoneyUtils;

import java.util.Calendar;
import java.util.List;

/*
* 显示记账：显示列表视图中AccountBean对象的数据*/
public class AccountAdapter extends BaseAdapter {
    Context context;
    List<AccountBean> mDatas;
    LayoutInflater inflater;
    int year,month,day;
    
    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(AccountBean bean);
    }
    
    private OnItemClickListener onItemClickListener;
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AccountAdapter(Context context, List<AccountBean> mDatas) {
        //列表用于存储显示的数据
        this.context = context;
        this.mDatas = mDatas;
        inflater = LayoutInflater.from(context);//获取LayoutInflater，用于解析布局文件
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        // 获取当前日期，并分别获取年、月、日。
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }//重写getCount()方法，返回数据列表中元素的个数。

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }//重写getItem()方法，返回指定位置的AccountBean对象。

    @Override
    public long getItemId(int position) {
        return mDatas.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 重写getView()方法，负责创建或更新列表视图中的每个视图。
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mainlv,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }//判断convertView是否为空，如果为空，则inflate一个新的视图，否则复用之前的视图。

        AccountBean bean = mDatas.get(position);
        // 获取当前位置的AccountBean对象
        holder.typeIv.setImageResource(bean.getsImageId());
        //根据AccountBean中的图片ID设置图片。
        holder.typeTv.setText(bean.getTypename());
        String beizhu = bean.getBeizhu();
        if (TextUtils.isEmpty(beizhu)) {
            holder.beizhuTv.setVisibility(View.GONE);
        } else {
            holder.beizhuTv.setVisibility(View.VISIBLE);
            holder.beizhuTv.setText(beizhu);
        }
        holder.moneyTv.setText(MoneyUtils.formatYuan(bean.getMoney()));
        int moneyColor = bean.getKind() == 1 ? R.color.income : R.color.outcome;
        holder.moneyTv.setTextColor(context.getColor(moneyColor));
        if (bean.getYear() == year && bean.getMonth() == month && bean.getDay() == day) {
            String timeText = bean.getTime();
            int spaceIndex = timeText != null ? timeText.indexOf(' ') : -1;
            if (spaceIndex > 0 && spaceIndex < timeText.length() - 1) {
                holder.timeTv.setText("今天 " + timeText.substring(spaceIndex + 1));
            } else {
                holder.timeTv.setText(timeText);
            }
        } else {
            holder.timeTv.setText(bean.getTime());
        }
        
        // 设置点击事件
        final AccountBean clickBean = bean;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(clickBean);
                }
            }
        });
        
        return convertView;
    }

    class ViewHolder{//定义一个内部类，用于存储布局中的视图引用
        View itemView;  // 添加整体视图引用
        ImageView typeIv;
        TextView typeTv,beizhuTv,timeTv,moneyTv;
        public ViewHolder(View view){
            itemView = view;
            typeIv = view.findViewById(R.id.item_mainlv_iv);
            typeTv = view.findViewById(R.id.item_mainlv_tv_title);
            timeTv = view.findViewById(R.id.item_mainlv_tv_time);
            beizhuTv = view.findViewById(R.id.item_mainlv_tv_beizhu);
            moneyTv = view.findViewById(R.id.item_mainlv_tv_money);

        }
    }
}
