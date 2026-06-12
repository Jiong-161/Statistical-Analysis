package com.example.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.accounts.db.AccountBean;
import com.example.accounts.db.DBManager;
import com.example.accounts.frag_record.IncomeFragment;
import com.example.accounts.adapter.RecordPagerAdapter;
import com.example.accounts.frag_record.OutcomeFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**/
public class RecordActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView titleTv;
    ImageView backIv;
    
    private boolean isEditMode = false;
    private int accountId = -1;
    private int accountKind = 0;  // 0-支出，1-收入
    
    private OutcomeFragment outcomeFragment;
    private IncomeFragment incomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        
        // 获取传递的数据
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            isEditMode = true;
            accountId = intent.getIntExtra("id", -1);
            accountKind = intent.getIntExtra("kind", 0);
        }
        
        //1.查找控件
        tabLayout = findViewById(R.id.record_tabs);
        viewPager = findViewById(R.id.record_vp);
        titleTv = findViewById(R.id.record_tv_title);
        backIv = findViewById(R.id.record_iv_back);
        
        // 设置标题
        if (isEditMode) {
            titleTv.setText("编辑记录");
        } else {
            titleTv.setText("记一笔");
        }
        
        // 返回按钮点击
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        //2.设置ViewPager加载页面
        initPager();
    }

    private void initPager() {
//        初始化ViewPager页面的集合
        List<Fragment> fragmentList = new ArrayList<>();
//        创建收入和支出页面，放置在Fragment当中
        outcomeFragment = new OutcomeFragment(); //支出
        incomeFragment = new IncomeFragment(); //收入
        
        // 如果是编辑模式，设置编辑模式
        if (isEditMode && accountId > 0) {
            outcomeFragment.setEditMode(accountId);
            incomeFragment.setEditMode(accountId);
        }
        
        fragmentList.add(outcomeFragment);
        fragmentList.add(incomeFragment);

//        创建适配器
        RecordPagerAdapter pagerAdapter = new RecordPagerAdapter(
                getSupportFragmentManager(), fragmentList);
//        设置适配器
        viewPager.setAdapter(pagerAdapter);
        //将TabLayout和ViwePager进行关联
        tabLayout.setupWithViewPager(viewPager);
        
        // 如果是编辑模式，根据类型切换到对应的tab
        if (isEditMode) {
            viewPager.setCurrentItem(accountKind);
        }
    }

    /* 点击事件*/
    public void onClick(View view) {
//        switch (view.getId()) {
//        case R.id.record_iv_back:
//            finish();
//            break;
//        }
        if (view.getId() == R.id.record_iv_back) {
            finish();
        }


    }
}
