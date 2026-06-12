package com.example.accounts.frag_record;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.accounts.R;
import com.example.accounts.db.AccountBean;
import com.example.accounts.db.DBManager;
import com.example.accounts.db.TypeBean;
import com.example.accounts.utils.BeiZhuDialog;
import com.example.accounts.utils.KeyBoardUtils;
import com.example.accounts.utils.SelectTimeDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
/*
* 此java页面:公告管理：支出和收入逻辑代码*/


public abstract class BaseRecordFragment extends Fragment implements View.OnClickListener {

    KeyboardView keyboardView;//自定义键盘
    EditText moneyEt;
    ImageView typeIv;
    TextView typeTv,beizhuTv,timeTv;
    GridView typeGv;
    List<TypeBean> typeList;
    TypeBaseAdapter adapter;
    AccountBean accountBean;//将需要插入到记账本当中的数据保存成对象的形式
    
    protected boolean isEditMode = false;//是否为编辑模式
    protected int accountId = -1;//要编辑的记录ID
    private AccountBean pendingEditBean = null;//暂存待编辑的数据

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBean = new AccountBean();//创建对象
        accountBean.setTypename("其他");
        //由子类设置相应的默认图标
        accountBean.setsImageId(getDefaultImageId());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_outcome_fragment, container, false);
        initView(view);
        if (isEditMode && accountId > 0) {
            //如果是编辑模式，先加载数据
            loadAccountData(accountId);
        } else {
            setInitTime();
        }
        //给GridView填充数据的方法
        loadDataToGV();
        setGVListener();//设置GridView每一项的点击事件
        return view;
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //视图创建完成后，填充待编辑的数据
        if (pendingEditBean != null) {
            fillDataToView(pendingEditBean);
            pendingEditBean = null;
        }
    }
    
    //设置编辑模式
    public void setEditMode(int id) {
        this.isEditMode = true;
        this.accountId = id;
    }
    
    //加载要编辑的记录数据
    protected void loadAccountData(int id) {
        //从数据库加载数据
        AccountBean bean = DBManager.getAccountItemById(id);
        if (bean != null) {
            if (getView() != null) {
                //视图已创建，直接填充
                fillDataToView(bean);
            } else {
                //视图尚未创建，暂存数据
                this.pendingEditBean = bean;
            }
        }
    }
    
    //填充已有数据到界面
    public void fillDataToView(AccountBean bean) {
        if (bean == null) return;
        
        this.accountBean = bean;
        
        //填充金额
        moneyEt.setText(String.valueOf(bean.getMoney()));
        //填充类型
        typeTv.setText(bean.getTypename());
        typeIv.setImageResource(bean.getsImageId());
        //填充备注
        if (!TextUtils.isEmpty(bean.getBeizhu())) {
            beizhuTv.setText(bean.getBeizhu());
        }
        //填充时间
        timeTv.setText(bean.getTime());
        
        //选中对应的类型
        selectTypeByImageId(bean.getsImageId());
    }
    
    //根据图片ID选中对应的类型
    protected void selectTypeByImageId(int sImageId) {
        for (int i = 0; i < typeList.size(); i++) {
            TypeBean typeBean = typeList.get(i);
            if (typeBean.getsImageId() == sImageId) {
                adapter.selectPos = i;
                adapter.notifyDataSetInvalidated();
                break;
            }
        }
    }

    /* 获取当前时间，显示在timeTv上*/
    private void setInitTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = sdf.format(date);
        timeTv.setText(time);
        accountBean.setTime(time);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        accountBean.setYear(year);
        accountBean.setMonth(month);
        accountBean.setDay(day);
    }

    /* 设置GridView每一项的点击事件
    * setGVListener方法设置typeGv的点击事件，当用户点击GridView中的项时，
    * 更新typeTv和typeIv的显示，并更新accountBean对象*/
    private void setGVListener() {
        typeGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selectPos = position;
                adapter.notifyDataSetInvalidated();//提示绘制发生变化了
                TypeBean typeBean = typeList.get(position);
                String typename = typeBean.getTypename();
                typeTv.setText(typename);
                accountBean.setTypename(typename);
                int sImageId = typeBean.getsImageId();
                typeIv.setImageResource(sImageId);
                accountBean.setsImageId(sImageId);
            }
        });
    }

    /* 给GridView填出数据的方法*/
    public void loadDataToGV() {
        typeList = new ArrayList<>();
        adapter = new TypeBaseAdapter(getContext(), typeList);
        typeGv.setAdapter(adapter);
    }

    private void initView(View view) {
        keyboardView = view.findViewById(R.id.frag_record_keyboard);
        moneyEt = view.findViewById(R.id.frag_record_et_money);
        typeIv = view.findViewById(R.id.frag_record_iv);
        typeGv = view.findViewById(R.id.frag_record_gv);
        typeTv = view.findViewById(R.id.frag_record_tv_type);
        beizhuTv = view.findViewById(R.id.frag_record_tv_beizhu);
        timeTv = view.findViewById(R.id.frag_record_tv_time);
        beizhuTv.setOnClickListener(this);
        timeTv.setOnClickListener(this);
        //让自定义软键盘显示出来
        KeyBoardUtils boardUtils = new KeyBoardUtils(keyboardView, moneyEt);
        boardUtils.showKeyboard();
        //设置接口，监听确定按钮按钮被点击了
        boardUtils.setOnEnsureListener(new KeyBoardUtils.OnEnsureListener() {
            @Override
            public void onEnsure() {
                //获取输入钱数
                String moneyStr = moneyEt.getText().toString();
                if (TextUtils.isEmpty(moneyStr) || moneyStr.equals("0")) {
                    getActivity().finish();
                    return;
                }
                float money = Float.parseFloat(moneyStr);
                if (money <= 0) {
                    return;
                }
                accountBean.setMoney(money);
                //获取记录的信息，保存在数据库当中
                saveAccountToDB();
                //返回上一级页面
                getActivity().finish();
            }
        });
    }
    /* 获取默认图标ID - 子类实现 */
    protected abstract int getDefaultImageId();
    
    /* 让子类一定要重写这个方法*/
    public abstract void saveAccountToDB();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.frag_record_tv_time:
            showTimeDialog();
            break;
        case R.id.frag_record_tv_beizhu:
            showBZDialog();
            break;
        }
    }
    /* 弹出显示时间的对话框*/
    private void showTimeDialog() {
        SelectTimeDialog dialog = new SelectTimeDialog(getContext());
        dialog.show();
        //设定确定按钮被点击了的监听器
        dialog.setOnEnsureListener(new SelectTimeDialog.OnEnsureListener() {
            @Override
            public void onEnsure(String time, int year, int month, int day) {
                timeTv.setText(time);
                accountBean.setTime(time);
                accountBean.setYear(year);
                accountBean.setMonth(month);
                accountBean.setDay(day);
            }
        });
    }

    /* 弹出备注对话框*/
    public void showBZDialog() {
        final BeiZhuDialog dialog = new BeiZhuDialog(getContext());
        //如果已有备注，先设置到对话框中
        if (!TextUtils.isEmpty(beizhuTv.getText()) && !beizhuTv.getText().equals("备注")) {
            dialog.setEditText(beizhuTv.getText().toString());
        }
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BeiZhuDialog.OnEnsureListener() {
            @Override
            public void onEnsure() {
                String msg = dialog.getEditText();
                if (!TextUtils.isEmpty(msg)) {
                    beizhuTv.setText(msg);
                    accountBean.setBeizhu(msg);
                }
                dialog.cancel();
            }
        });
    }
}
