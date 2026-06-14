package com.example.accounts.frag_record;


import android.graphics.Color;

import com.example.accounts.R;
import com.example.accounts.db.DBManager;
import com.example.accounts.db.TypeBean;

import java.util.List;
/*
 * 收入记录页面
 */
/*
  A simple {@link Fragment} subclass.

 */


public class IncomeFragment extends BaseRecordFragment {

    @Override
    protected int getDefaultImageId() {
        return R.drawable.in_qt_fs;
    }

    @Override
    protected void setMoneyColor() {
        // 收入页面：金额显示为绿色
        moneyEt.setTextColor(Color.parseColor("#10B981"));
    }

    @Override
    public void loadDataToGV() {
        super.loadDataToGV();
        //获取数据库当中的数据源，
        //从数据库中查询与参数1相关的TypeBean对象列表，并将结果存储在inlist列表中
        List<TypeBean> inlist = DBManager.getTypeList(1);

        typeList.addAll(inlist);//用于存储TypeBean对象
        adapter.notifyDataSetChanged();//通知与GridView关联的适配器
        //数据集已经发生了改变。因此，GridView会重新绘制以反映这些更改。
        //这意味着任何绑定到typeList的GridView都会更新其内容以显示新的数据。
        //如果是编辑模式，不重置默认值
        if (!isEditMode) {
            typeTv.setText("其他");
            typeIv.setImageResource(R.drawable.in_qt_fs);
        }
    }

    @Override
    public void saveAccountToDB() {
        accountBean.setKind(1);
        if (isEditMode && accountId > 0) {
            //编辑模式，更新数据
            DBManager.updateItemInAccounttb(accountBean);
        } else {
            //新增模式，插入数据
            DBManager.insertItemToAccounttb(accountBean);
        }
    }
    //定义了一个名为saveAccountToDB的方法，该方法首先将accountBean对象的kind属性设置为1，
    //然后调用DBManager类的insertItemToAccounttb方法
    //将accountBean对象的信息保存到数据库的accounttb表中。
}
    /*
    * 将一个账户对象（accountBean）的kind属性设置为1，
    * 并将该账户对象的信息保存到数据库中。*/
//IncomeFragment类

//    @Override
//    public void onClick(View v) {
//
//    }


//    @Override
//    public void saveAccountToDB() {
//        accountBean.setKind(1);
//        DBManager.insertItemToAccounttb(accountBean);
//    }
