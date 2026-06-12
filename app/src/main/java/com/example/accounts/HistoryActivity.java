package com.example.accounts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.accounts.adapter.AccountAdapter;
import com.example.accounts.db.AccountBean;
import com.example.accounts.db.DBManager;
import com.example.accounts.utils.CalendarDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//查询历史记录
public class HistoryActivity extends AppCompatActivity {


    List<AccountBean>mDatas;
    ListView history;//声明对象
    TextView time;
    int year,month;//年，月
    //数据源

    AccountAdapter adapter;//调用首页的适配器
    //选中的位置
    int dialogSelPos = -1;
    int dialogSelMonth = -1;
  // private AdapterView<Adapter> historyLv;
    //private ListView historyLv; // 声明 ListView 对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);//
        history=findViewById(R.id.history_lv);//获取布局文件名为history_lv列表控件，
                                                        // 并将赋值于history
        time=findViewById(R.id.history_tv_time);
        mDatas=new ArrayList<>();
        //设置适配器
        adapter =new AccountAdapter(this,mDatas);//集合
        history.setAdapter(adapter);
        //初始化时间和加载数据
        initTime();
        time.setText(year+"年"+month+"月");
        loadData(year,month);//加载数据
        setLVClickListener();//长按事件：删除


    }
    /* 获取指定年份月份收支情况的列表*/
    private void loadData(int year, int month) {
        //调用数据库DBManager的记账表当中某一月的所有支出或者收入情况
        List<AccountBean> list = DBManager.getAccountListOneMonthFromAccounttb(year, month);
        mDatas.clear();//清空
        mDatas.addAll(list);//将list中的所有元素添加到mDatas列表中
        adapter.notifyDataSetChanged();//通知数据绑定的适配更新


    }

    //获取日历对象
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;

    }
    /*设置ListView每一个item的长按事件*/
    private void setLVClickListener() {

        history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AccountBean accountBean = mDatas.get(position);
                deleteItem(accountBean);
                return false;
            }
        });
    }
//删除条目的方法
    private void deleteItem(final AccountBean accountBean) {
        final int delId = accountBean.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录么？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager.deleteItemFromAccounttbById(delId);
                        mDatas.remove(accountBean);   //实时刷新，从数据源删除
                        adapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }
    //重写onClick（）方法，点击跳转。
    public void onClick(View view) {
        switch (view.getId()){
        case R.id.history_iv_back:
            finish();
            break;
        case R.id.history_iv_rili:
            // 处理日历对话框相关操作
            CalendarDialog dialog = new CalendarDialog(this,dialogSelPos,dialogSelMonth);
            dialog.show();
           dialog.setDialogSize();
           dialog.setOnRefreshListener(new CalendarDialog.OnRefreshListener() {
                @Override
                public void onRefresh(int selPos, int year, int month) {
                    time.setText(year+"年"+month+"月");
                    loadData(year,month);
                   dialogSelPos = selPos;
                    dialogSelMonth = month;
                }
            });
            break;
        }
        }
    }
