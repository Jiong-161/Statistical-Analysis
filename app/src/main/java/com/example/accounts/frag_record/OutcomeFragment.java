package com.example.accounts.frag_record;

import android.graphics.Color;
import android.view.View;

import com.example.accounts.R;
import com.example.accounts.db.DBManager;
import com.example.accounts.db.TypeBean;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutcomeFragment extends BaseRecordFragment {

    @Override
    protected int getDefaultImageId() {
        return R.drawable.ic_qita_fs;
    }

    @Override
    protected void setMoneyColor() {
        // 支出页面：金额显示为红色
        moneyEt.setTextColor(Color.parseColor("#EF4444"));
    }

    // 重写
    @Override
    public void loadDataToGV() {
        super.loadDataToGV();
        //获取数据库当中的数据源
        List<TypeBean> outlist = DBManager.getTypeList(0);
        typeList.addAll(outlist);
        adapter.notifyDataSetChanged();
        // 如果是编辑模式，不重置默认值
        if (!isEditMode) {
            typeTv.setText("其他");
            typeIv.setImageResource(R.drawable.ic_qita_fs);
        }
    }

    @Override
    public void saveAccountToDB() {
        accountBean.setKind(0);
        if (isEditMode && accountId > 0) {
            //编辑模式，更新数据
            DBManager.updateItemInAccounttb(accountBean);
        } else {
            //新增模式，插入数据
            DBManager.insertItemToAccounttb(accountBean);
        }
    }

//    @Override
//    public void onClick(View v) {
//
//
//    }
}
