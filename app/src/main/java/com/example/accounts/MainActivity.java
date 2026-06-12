package com.example.accounts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.accounts.adapter.AccountAdapter;
import com.example.accounts.db.AccountBean;
import com.example.accounts.db.DBManager;
import com.example.accounts.utils.BudgetDialog;
import com.example.accounts.utils.MoneyUtils;
import com.example.accounts.utils.MoreDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*主要功能是显示今日的收支情况，并提供了编辑、搜索、更多等功能，同时提供了预算设置和显示/隐藏功能。*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView todayLv;  //展示今日收支情况的ListView
    ImageButton searchIv;//设置搜索信息
    Button editBtn;//点击记账页面
    ImageButton moreBtn;//更多菜单
    //声明数据源
    List<AccountBean> mDatas;//创建一个 List 的对象 mDatas，
    AccountAdapter adapter;//声明适配器
    int year,month,day;
    //头布局相关控件
    View headerView;//声明对象
    TextView topOutTv,topInTv,topbudgetTv,topConTv;
    ImageView topShowIv;
    SharedPreferences preferences;//加载共享参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime();
        initView();
        preferences = getSharedPreferences("budget", Context.MODE_PRIVATE);
        //添加ListView的头布局
        addLVHeaderView();

        mDatas = new ArrayList<>();
        //设置适配器：加载每一行数据到列表当中
        adapter = new AccountAdapter(this, mDatas);
        todayLv.setAdapter(adapter);
        //设置点击事件
        setLVClickListener();
        /*创建一个 List 的对象 mDatas，并为它创建一个适配器 AccountAdapter，将适配器设置给 ListView。
        最后，调用 loadDBData() 方法从数据库中加载数据，
        并刷新 ListView 的显示*/
    }
    
    /* 设置ListView的点击事件 */
    private void setLVClickListener() {
        adapter.setOnItemClickListener(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(AccountBean bean) {
                // 显示操作对话框
                showItemOperationDialog(bean);
            }
        });
    }
    
    /* 显示项目操作对话框：编辑或删除 */
    private void showItemOperationDialog(AccountBean bean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"编辑记录", "删除记录"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // 编辑记录
                    Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                    intent.putExtra("id", bean.getId());
                    intent.putExtra("kind", bean.getKind());
                    startActivity(intent);
                } else if (which == 1) {
                    // 删除记录
                    showDeleteItemDialog(bean);
                }
            }
        });
        builder.create().show();
    }
    
    /* 初始化自带的View的方法*/
    private void initView() {
        todayLv = findViewById(R.id.main_lv);
        editBtn = findViewById(R.id.main_btn_edit);
        moreBtn = findViewById(R.id.main_btn_more);
        searchIv = findViewById(R.id.main_iv_search);
        editBtn.setOnClickListener(this);
        moreBtn.setOnClickListener(this);
        searchIv.setOnClickListener(this);
        setLVLongClickListener();
    }
    /* 设置ListView的长按事件 setLVLongClickListener() 方法
    * 弹出一个对话框，提示用户是否删除这一项，如果用户点击确定，
    * 则会从数据库中删除这一项，并从 mDatas 中移除这一项，最后刷新 ListView 的显示。*/
    private void setLVLongClickListener() {
        todayLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {  //点击了头布局
                    return false;
                }
                int pos = position-1;
                AccountBean clickBean = mDatas.get(pos);  //获取正在被点击的这条信息

                //弹出提示用户是否删除的对话框
                showDeleteItemDialog(clickBean);
                return false;
            }
        });
    }
    /* 弹出是否删除某一条记录的对话框,showDeleteItemDialog() 方法*/
    private void showDeleteItemDialog(final AccountBean clickBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除记录");
        builder.setMessage("您确定要删除这条记录吗？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int click_id = clickBean.getId();
                //执行删除的操作
                DBManager.deleteItemFromAccounttbById(click_id);
                mDatas.remove(clickBean);   //实时刷新，移除集合当中的对象
                adapter.notifyDataSetChanged();   //提示适配器更新数据
                setTopTvShow();   //改变头布局TextView显示的内容
            }
        });
        builder.create().show();   //显示对话框
    }

    /* 给ListView添加头布局的方法
    addLVHeaderView() 方法
    * 为头布局设置了点击事件的监听器，当点击头布局时，
    * 会跳转到 MonthChartActivity 中。同时，为头布局中的控件查找可用控件，并为它们设置点击事件的监听器*/
    private void addLVHeaderView() {
        //将布局转换成View对象
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        //查找头布局可用控件
        topOutTv = headerView.findViewById(R.id.item_mainlv_top_tv_out);
        //在headerView中查找ID为item_mainlv_top_tv_out的视图，并将找到的视图赋值给topOutTv变量。
        topInTv = headerView.findViewById(R.id.item_mainlv_top_tv_in);
        topbudgetTv = headerView.findViewById(R.id.item_mainlv_top_tv_budget);
        topConTv = headerView.findViewById(R.id.item_mainlv_top_tv_day);
        topShowIv = headerView.findViewById(R.id.item_mainlv_top_iv_hide);

        topbudgetTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
        topShowIv.setOnClickListener(this);

    }
    /* 获取今日的具体时间:年，月，日*/
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /** 当activity获取焦点时，会调用的方法
     * 加载数据库中的数据，并刷新 ListView 的显示，同时设置头布局中文本内容的显示。*/
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();
    }
    /* 设置头布局当中文本内容的显示*/
    private void setTopTvShow() {
        //获取今日支出和收入总金额，显示在view当中
        float incomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 1);
        float outcomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 0);
        String infoOneDay = "今日支出 " + MoneyUtils.formatYuanCompact(outcomeOneDay)
                + "  收入 " + MoneyUtils.formatYuanCompact(incomeOneDay);
        topConTv.setText(infoOneDay);
        float incomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
        topInTv.setText(MoneyUtils.formatYuanCompact(incomeOneMonth));
        topOutTv.setText(MoneyUtils.formatYuanCompact(outcomeOneMonth));

        float bmoney = preferences.getFloat("bmoney", 0);
        if (bmoney == 0) {
            topbudgetTv.setText("￥ 0");
        } else {
            float syMoney = MoneyUtils.subtract(bmoney, outcomeOneMonth);
            topbudgetTv.setText(MoneyUtils.formatYuanCompact(syMoney));
        }
    }

    //loadDBData方法从数据库中加载数据，并刷新ListView的显示
    private void loadDBData() {
        List<AccountBean> list = DBManager.getAccountListOneDayFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

/**onClick方法处理了几个按钮和ImageView的点击事件，包括搜索按钮、编辑按钮、更多按钮、
 * 预算TextView和显示/隐藏按钮的点击事件*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.main_iv_search:
            Intent it = new Intent(this, SearchActivity.class);  //跳转界面
            startActivity(it);
            break;
        case R.id.main_btn_edit:
            Intent it1 = new Intent(this, RecordActivity.class);  //跳转界面
            startActivity(it1);
            break;
        case R.id.main_btn_more:
            MoreDialog moreDialog = new MoreDialog(this);
            moreDialog.show();
            moreDialog.setDialogSize();
            break;
        case R.id.item_mainlv_top_tv_budget:
            showBudgetDialog();
            break;
        case R.id.item_mainlv_top_iv_hide:
            // 切换TextView明文和密文
            toggleShow();
            break;
        }
        if (v == headerView) {
            //头布局被点击了，跳转到图表统计界面
            Intent intent = new Intent();
            intent.setClass(this, ChartActivity.class);
            startActivity(intent);
        }
    }
    /* 显示运算设置对话框，
    * 显示一个预算设置对话框，当用户点击确定时，
    * 将预算金额写入到共享参数当中，进行存储，并计算剩余金额。*/
    private void showBudgetDialog() {
        BudgetDialog dialog = new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                //将预算金额写入到共享参数当中，进行存储
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("bmoney",money);
                editor.apply();
                float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
                float syMoney = MoneyUtils.subtract(money, outcomeOneMonth);
                topbudgetTv.setText(MoneyUtils.formatYuanCompact(syMoney));
            }
        });
    }

    boolean isShow = true;//成员变量
    /*toggleShow() 方法中，点击头布局眼睛时，如果原来是明文，就加密，如果是密文，就显示出来。
     * 点击头布局眼睛时，如果原来是明文，就加密，如果是密文，就显示出来
     * PasswordTransformationMethod 密码转换方法
     * */
    private void toggleShow() {
        if (isShow) {   //明文====》密文
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            topInTv.setTransformationMethod(passwordMethod);   //设置隐藏
            topOutTv.setTransformationMethod(passwordMethod);   //设置隐藏
            topbudgetTv.setTransformationMethod(passwordMethod);   //设置隐藏
            topShowIv.setImageResource(R.drawable.ih_hide);
            isShow = false;   //设置标志位为隐藏状态
        } else {  //密文---》明文
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            topInTv.setTransformationMethod(hideMethod);   //设置隐藏
            topOutTv.setTransformationMethod(hideMethod);   //设置隐藏
            topbudgetTv.setTransformationMethod(hideMethod);   //设置隐藏
            topShowIv.setImageResource(R.drawable.ih_show);
            isShow = true;   //设置标志位为隐藏状态
        }
    }
}
