package com.example.accounts.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/*创建适配器
* 在Fragment列表与ViewPager结合使用。当你为ViewPager设置这个适配器时，
* ViewPager会根据fragmentList中的Fragment来创建页面，
* 并使用titles数组中的标题作为各个页面的标题。
* 滑动ViewPager时，根据当前的位置从fragmentList中获取对应的Fragment来显示*/
public class RecordPagerAdapter extends FragmentPagerAdapter {
    /*
    * 继承FragmentPagerAdapter
    * FragmentPagerAdapter是Android中用于ViewPager与Fragment结合使用的一个适配器类*/
    List<Fragment>fragmentList;//Fragment的列表，用于存储ViewPager中各个页面所对应的Fragment。
    String[]titles = {"支出","收入"};

    public RecordPagerAdapter(@NonNull FragmentManager fm,List<Fragment>fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }/*
    构造函数：接受两个参数： FragmentManager ，Fragment列表
    */

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
