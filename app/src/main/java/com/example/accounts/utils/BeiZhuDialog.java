package com.example.accounts.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.accounts.R;
/*
* 弹出备注框*/

public class BeiZhuDialog extends Dialog implements View.OnClickListener {
    EditText et;
    Button cancelBtn,ensureBtn;
    OnEnsureListener onEnsureListener;
    // 设定回调接口的方法
    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public BeiZhuDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_beizhu);//设置对话框显示布局
        et = findViewById(R.id.dialog_beizhu_et);
        cancelBtn = findViewById(R.id.dialog_beizhu_btn_cancel);
        ensureBtn = findViewById(R.id.dialog_beizhu_btn_ensure);
        cancelBtn.setOnClickListener(this);
        ensureBtn.setOnClickListener(this);
    }
    public interface OnEnsureListener{
        public void onEnsure();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_beizhu_btn_cancel) {
            cancel();
        } else if (v.getId() == R.id.dialog_beizhu_btn_ensure) {
            if (onEnsureListener != null) {
                onEnsureListener.onEnsure();
            }
        }
        // 如果你想处理其他可能的视图点击事件，可以在这里添加更多的else if块
        // 例如:
        // else if (v.getId() == R.id.some_other_button) {
        //     // 处理其他按钮的点击事件
        // }
        // 如果没有其他需要处理的视图，则不需要添加额外的else块
    }

    //    获取输入数据的方法
    public String getEditText(){
        return et.getText().toString().trim();
    }
    
    //    设置输入数据的方法
    public void setEditText(String text){
        if (et != null && text != null) {
            et.setText(text);
            et.setSelection(text.length());  // 光标移动到末尾
        }
    }
    /* 设置Dialog的尺寸和屏幕尺寸一致*/
    public void setDialogSize(){
//        获取当前窗口对象
        Window window = getWindow();
//        获取窗口对象的参数
        WindowManager.LayoutParams wlp = window.getAttributes();
//        获取屏幕宽度
        Display d = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int)(d.getWidth());  //对话框窗口为屏幕窗口
        wlp.gravity = Gravity.BOTTOM;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
        handler.sendEmptyMessageDelayed(1,100);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //自动弹出软键盘的方法
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };
}
