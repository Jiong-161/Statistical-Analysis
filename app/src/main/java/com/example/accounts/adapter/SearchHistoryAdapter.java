package com.example.accounts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.accounts.R;
import com.example.accounts.db.SearchHistoryBean;

import java.util.List;

/**
 * 搜索历史记录的适配器
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private Context context;
    private List<SearchHistoryBean> historyList;
    private LayoutInflater inflater;
    private OnHistoryItemClickListener listener;

    public interface OnHistoryItemClickListener {
        void onItemClick(String keyword);
        void onDeleteClick(int id);
    }

    public SearchHistoryAdapter(Context context, List<SearchHistoryBean> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_search_history, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchHistoryBean bean = historyList.get(position);
        holder.keywordTv.setText(bean.getKeyword());

        final int pos = position;
        final String keyword = bean.getKeyword();
        final int id = bean.getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(keyword);
                }
            }
        });

        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(id);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView keywordTv;
        ImageView deleteIv;
        View itemView;

        public ViewHolder(View view) {
            itemView = view;
            keywordTv = view.findViewById(R.id.tv_history_keyword);
            deleteIv = view.findViewById(R.id.iv_history_delete);
        }
    }
}
