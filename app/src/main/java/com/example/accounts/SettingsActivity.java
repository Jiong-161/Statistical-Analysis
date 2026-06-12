package com.example.accounts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.accounts.utils.BudgetDialog;
import com.example.accounts.utils.DataBackupUtils;
//设置页面
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    
    private ImageView backIv;
    private TextView budgetTv;
    private RelativeLayout budgetRl, budgetRemindRl, backupRl, restoreRl, clearRl, passwordRl, aboutRl;
    private Switch budgetSwitch;
    
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initView();
        initData();
    }
    
    private void initView() {
        backIv = findViewById(R.id.settings_iv_back);
        budgetTv = findViewById(R.id.settings_tv_budget);
        budgetRl = findViewById(R.id.settings_rl_budget);
        budgetRemindRl = findViewById(R.id.settings_rl_budget_remind);
        backupRl = findViewById(R.id.settings_rl_backup);
        restoreRl = findViewById(R.id.settings_rl_restore);
        clearRl = findViewById(R.id.settings_rl_clear);
        passwordRl = findViewById(R.id.settings_rl_password);
        aboutRl = findViewById(R.id.settings_rl_about);
        budgetSwitch = findViewById(R.id.settings_switch_budget);
        
        backIv.setOnClickListener(this);
        budgetRl.setOnClickListener(this);
        budgetRemindRl.setOnClickListener(this);
        backupRl.setOnClickListener(this);
        restoreRl.setOnClickListener(this);
        clearRl.setOnClickListener(this);
        passwordRl.setOnClickListener(this);
        aboutRl.setOnClickListener(this);
        
        // 设置预算提醒开关监听器
        budgetSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("budget_remind", isChecked);
                editor.apply();
                Toast.makeText(SettingsActivity.this, isChecked ? "已开启预算提醒" : "已关闭预算提醒", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void initData() {
        preferences = getSharedPreferences("budget", Context.MODE_PRIVATE);
        
        // 显示当前预算
        float budget = preferences.getFloat("bmoney", 0);
        if (budget > 0) {
            budgetTv.setText("￥" + budget);
            budgetTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        
        // 恢复预算提醒开关状态
        boolean remindEnabled = preferences.getBoolean("budget_remind", false);
        budgetSwitch.setChecked(remindEnabled);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_iv_back:
                finish();
                break;
            case R.id.settings_rl_budget:
                showBudgetDialog();
                break;
            case R.id.settings_rl_backup:
                backupData();
                break;
            case R.id.settings_rl_restore:
                restoreData();
                break;
            case R.id.settings_rl_clear:
                showClearDataDialog();
                break;
            case R.id.settings_rl_password:
                changePassword();
                break;
            case R.id.settings_rl_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
    }
    
    private void showBudgetDialog() {
        BudgetDialog dialog = new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("bmoney", money);
                editor.apply();
                budgetTv.setText("￥" + money);
                budgetTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                Toast.makeText(SettingsActivity.this, "预算设置成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void backupData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("数据备份");
        builder.setMessage("是否备份所有数据到本地存储？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean success = DataBackupUtils.backupData(SettingsActivity.this);
                if (success) {
                    Toast.makeText(SettingsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
    
    private void restoreData() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("数据恢复");
        builder.setMessage("从备份文件恢复数据？\n（会覆盖当前数据，请谨慎操作！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean success = DataBackupUtils.restoreData(SettingsActivity.this);
                if (success) {
                    Toast.makeText(SettingsActivity.this, "恢复成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "恢复失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
    
    private void showClearDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清除数据");
        builder.setMessage("确定要清除所有数据吗？\n此操作不可恢复，请谨慎操作！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean success = DataBackupUtils.clearAllData(SettingsActivity.this);
                if (success) {
                    Toast.makeText(SettingsActivity.this, "数据已清除", Toast.LENGTH_SHORT).show();
                    budgetTv.setText("未设置");
                    budgetTv.setTextColor(getResources().getColor(R.color.color_text_grey));
                } else {
                    Toast.makeText(SettingsActivity.this, "清除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }
    
    private void changePassword() {
        Toast.makeText(this, "请前往登录界面修改密码", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, userActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 刷新预算显示
        float budget = preferences.getFloat("bmoney", 0);
        if (budget > 0) {
            budgetTv.setText("￥" + budget);
            budgetTv.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
