package com.example.accounts.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.accounts.R;

/*
 * 在记录页面弹出时间对话框
 * */
public class SelectTimeDialog extends Dialog implements View.OnClickListener {
    EditText hourEt,minuteEt;
    DatePicker datePicker;
    Button ensureBtn,cancelBtn;
    public interface OnEnsureListener{
        public void onEnsure(String time,int year,int month,int day);
    }
    OnEnsureListener onEnsureListener;

    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public SelectTimeDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time);
        hourEt = findViewById(R.id.dialog_time_et_hour);
        minuteEt = findViewById(R.id.dialog_time_et_minute);
        datePicker = findViewById(R.id.dialog_time_dp);
        ensureBtn = findViewById(R.id.dialog_time_btn_ensure);
        cancelBtn = findViewById(R.id.dialog_time_btn_cancel);
        ensureBtn.setOnClickListener(this);  //添加点击监听事件
        cancelBtn.setOnClickListener(this);
        hideDatePickerHeader();
    }
/**/
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_time_btn_cancel) {
            // 当点击取消按钮时，调用cancel()方法
            cancel();
        } else if (v.getId() == R.id.dialog_time_btn_ensure) {
            // 当点击确认按钮时，执行确认操作（比如处理时间）
            // 这里只是示例，您应该替换为您的实际处理逻辑
            cancel(); // 假设您有一个confirmTime()方法来处理时间确认
        }
        // 如果还有其他可能的View ID，可以继续添加else if
        // 如果想要处理没有匹配的情况，可以添加else块
        // else {
        //     // 如果没有匹配的ID，可以添加默认处理（如果需要的话）
        // }


    int year = datePicker.getYear();  //选择年份
            int month = datePicker.getMonth()+1;
            int dayOfMonth = datePicker.getDayOfMonth();
            String monthStr = String.valueOf(month);
            if (month<10){
                monthStr = "0"+month;
            }
            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth<10){
                dayStr="0"+dayOfMonth;
            }
//              获取输入的小时和分钟
            String hourStr = hourEt.getText().toString();
            String minuteStr = minuteEt.getText().toString();
            int hour = 0;
            if (!TextUtils.isEmpty(hourStr)) {
                hour = Integer.parseInt(hourStr);
                hour=hour%24;
            }
            int minute = 0;
            if (!TextUtils.isEmpty(minuteStr)) {
                minute = Integer.parseInt(minuteStr);
                minute=minute%60;
            }

            hourStr=String.valueOf(hour);
            minuteStr=String.valueOf(minute);
            if (hour<10){
                hourStr="0"+hour;
            }
            if (minute<10){
                minuteStr="0"+minute;
            }
            String timeFormat = year+"年"+monthStr+"月"+dayStr+"日 "+hourStr+":"+minuteStr;
            if (onEnsureListener!=null) {
                onEnsureListener.onEnsure(timeFormat,year,month,dayOfMonth);
            }
            cancel();

        }
//    @Override
//public void onClick(View v) {
//    if (v.getId() == R.id.dialog_time_btn_cancel) {
//        // 当点击取消按钮时，调用cancel()方法
//        cancel();
//    } else if (v.getId() == R.id.dialog_time_btn_ensure) {
//        // 当点击确认按钮时，执行确认操作（比如处理时间）
//        int year = datePicker.getYear();  // 选择年份
//        int month = datePicker.getMonth() + 1;
//        int dayOfMonth = datePicker.getDayOfMonth();
//        String monthStr = String.valueOf(month);
//        if (month < 10) {
//            monthStr = "0" + month;
//        }
//        String dayStr = String.valueOf(dayOfMonth);
//        if (dayOfMonth < 10) {
//            dayStr = "0" + dayOfMonth;
//        }
//
//        // 获取输入的小时和分钟
//        String hourStr = hourEt.getText().toString();
//        String minuteStr = minuteEt.getText().toString();
//        int hour = 0;
//        if (!TextUtils.isEmpty(hourStr)) {
//            hour = Integer.parseInt(hourStr);
//            hour = hour % 24;
//        }
//        int minute = 0;
//        if (!TextUtils.isEmpty(minuteStr)) {
//            minute = Integer.parseInt(minuteStr);
//            minute = minute % 60;
//        }
//
//        hourStr = String.valueOf(hour);
//        minuteStr = String.valueOf(minute);
//        if (hour < 10) {
//            hourStr = "0" + hour;
//        }
//        if (minute < 10) {
//            minuteStr = "0" + minute;
//        }
//
//        String timeFormat = year + "年" + monthStr + "月" + dayStr + "日 " + hourStr + ":" + minuteStr;
//        if (onEnsureListener != null) {
//            onEnsureListener.onEnsure(timeFormat, year, month, dayOfMonth);
//        }
//
//        // 如果需要在确认后关闭对话框，可以调用cancel()
//        // cancel(); // 根据需求决定是否取消注释
//    }
//    // 如果还有其他可能的View ID，可以继续添加else if
//    // 如果想要处理没有匹配的情况，可以添加else块
//    // else {
//    //     // 如果没有匹配的ID，可以添加默认处理（如果需要的话）
//    // }
//}


    //隐藏DatePicker头布局
    private void hideDatePickerHeader(){
        ViewGroup rootView = (ViewGroup) datePicker.getChildAt(0);
        if (rootView == null) {
            return;
        }
        View headerView = rootView.getChildAt(0);
        if (headerView == null) {
            return;
        }
        //5.0+
        int headerId = getContext().getResources().getIdentifier("day_picker_selector_layout", "id", "android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
            layoutParamsRoot.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rootView.setLayoutParams(layoutParamsRoot);

            ViewGroup animator = (ViewGroup) rootView.getChildAt(1);
            ViewGroup.LayoutParams layoutParamsAnimator = animator.getLayoutParams();
            layoutParamsAnimator.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            animator.setLayoutParams(layoutParamsAnimator);

            View child = animator.getChildAt(0);
            ViewGroup.LayoutParams layoutParamsChild = child.getLayoutParams();
            layoutParamsChild.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            child.setLayoutParams(layoutParamsChild);
            return;
        }

        // 6.0+
        headerId = getContext().getResources().getIdentifier("date_picker_header","id","android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);
        }
    }
}

