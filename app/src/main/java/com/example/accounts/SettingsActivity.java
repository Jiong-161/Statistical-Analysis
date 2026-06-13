package com.example.accounts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.accounts.db.DBManager;
import com.example.accounts.db.UserBean;
import com.example.accounts.utils.BudgetDialog;
import com.example.accounts.utils.DataBackupUtils;
import com.example.accounts.utils.PasswordUtils;
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

    /**
     * 修改密码功能
     * 流程：弹出对话框 → 前端校验 → 校验旧密码 → 更新新密码 → 反馈结果
     */
    private void changePassword() {
        // 从SharedPreferences获取当前登录用户ID
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        int currentUserId = spf.getInt("current_user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建密码修改对话框布局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 20, 60, 0);

        final EditText etOldPass = new EditText(this);
        etOldPass.setHint("请输入旧密码");
        etOldPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etOldPass);

        final EditText etNewPass = new EditText(this);
        etNewPass.setHint("请输入新密码（至少6位）");
        etNewPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass);

        final EditText etConfirmPass = new EditText(this);
        etConfirmPass.setHint("请再次输入新密码");
        etConfirmPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPass);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("修改密码")
                .setView(layout)
                .setPositiveButton("确定", (dialog1, which) -> {
                    // 前端校验
                    String oldPass = etOldPass.getText().toString();
                    String newPass = etNewPass.getText().toString();
                    String confirmPass = etConfirmPass.getText().toString();

                    if (TextUtils.isEmpty(oldPass)) {
                        Toast.makeText(SettingsActivity.this, "请输入旧密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(newPass)) {
                        Toast.makeText(SettingsActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPass.length() < 6) {
                        Toast.makeText(SettingsActivity.this, "新密码至少6位", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.equals(newPass, confirmPass)) {
                        Toast.makeText(SettingsActivity.this, "两次新密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 查询用户信息用于校验旧密码
                    UserBean user = DBManager.queryUserByUsername(
                            spf.getString("current_username", ""));
                    if (user == null) {
                        Toast.makeText(SettingsActivity.this, "用户信息不存在，请重新登录",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 校验旧密码
                    if (!PasswordUtils.verifyPassword(oldPass, user.getPasswordHash(), user.getSalt())) {
                        Toast.makeText(SettingsActivity.this, "旧密码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 更新新密码（内部自动重新生成盐值）
                    boolean success = DBManager.updatePassword(currentUserId, newPass);
                    if (success) {
                        Toast.makeText(SettingsActivity.this, "密码修改成功，请重新登录",
                                Toast.LENGTH_SHORT).show();
                        // 清除登录态并跳转登录页
                        clearLoginState(spf);
                        Intent intent = new Intent(this, userActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SettingsActivity.this, "密码修改失败，请稍后重试",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .create();

        dialog.show();
    }

    /**
     * 清除登录态
     */
    private void clearLoginState(SharedPreferences spf) {
        SharedPreferences.Editor editor = spf.edit();
        editor.remove("current_user_id");
        editor.remove("current_username");
        editor.remove("isRemember");
        editor.apply();
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
