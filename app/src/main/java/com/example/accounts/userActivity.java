package com.example.accounts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.accounts.db.DBManager;
import com.example.accounts.db.UserBean;
import com.example.accounts.utils.PasswordUtils;

/**
 * 登录页面
 * <p>
 * 登录流程：输入校验 → 根据用户名查询用户 → SHA-256哈希比对密码
 * → 保存登录态到SharedPreferences → 跳转主页面
 * </p>
 */
public class userActivity extends AppCompatActivity {

    private static final String TAG = "userActivity";
    /**
     * 注册请求码
     */
    public static final int REQUEST_CODE_REGISTER = 2;
    private static final String SPF_NAME = "spfRecord";
    /**
     * SharedPreferences 键名常量
     */
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_CURRENT_USERNAME = "current_username";
    private static final String KEY_IS_REMEMBER = "isRemember";
    private Button btn1;
    private EditText etAccount, etPassword;
    private CheckBox cbRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setTitle("登录");

        initView();
        initData();
        initLoginButton();
    }

    /**
     * 初始化视图控件
     */
    private void initView() {
        btn1 = findViewById(R.id.btn_login);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
    }

    /**
     * 初始化数据（从SharedPreferences恢复记住的账号）
     */
    private void initData() {
        SharedPreferences spf = getSharedPreferences(SPF_NAME, MODE_PRIVATE);
        boolean isRemember = spf.getBoolean(KEY_IS_REMEMBER, false);
        String account = spf.getString(KEY_CURRENT_USERNAME, "");

        if (isRemember && !TextUtils.isEmpty(account)) {
            etAccount.setText(account);
            cbRemember.setChecked(true);
            // 密码不回填，仅回填账号名
        }
    }

    /**
     * 初始化登录按钮点击事件
     */
    private void initLoginButton() {
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    /**
     * 执行登录操作
     * 流程：输入校验 → 查询用户 → 哈希比对 → 保存登录态 → 跳转主页
     */
    private void performLogin() {
        // 1. 输入校验
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString();

        Log.d(TAG, "onClick: 登录账号=" + account);

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(userActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(userActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 根据用户名查询数据库中的用户信息
        UserBean user = DBManager.queryUserByUsername(account);

        // 3. 判断用户是否存在
        if (user == null) {
            Toast.makeText(userActivity.this, "该用户名尚未注册", Toast.LENGTH_LONG).show();
            return;
        }

        // 4. 检查账号状态
        if (user.getStatus() != 0) {
            Toast.makeText(userActivity.this, "该账号已被禁用", Toast.LENGTH_LONG).show();
            return;
        }

        // 5. 使用SHA-256+盐值哈希比对密码
        if (!PasswordUtils.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
            Toast.makeText(userActivity.this, "密码错误", Toast.LENGTH_LONG).show();
            return;
        }

        // 6. 密码正确，保存登录态到SharedPreferences
        saveLoginState(account, user.getId());

        // 7. 跳转到主页面
        Toast.makeText(userActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(userActivity.this, MainActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
        finish();
    }

    /**
     * 保存登录状态到SharedPreferences
     *
     * @param account 用户名
     * @param userId  用户ID
     */
    private void saveLoginState(String account, int userId) {
        SharedPreferences spf = getSharedPreferences(SPF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = spf.edit();

        edit.putInt(KEY_CURRENT_USER_ID, userId);
        edit.putString(KEY_CURRENT_USERNAME, account);
        edit.putBoolean(KEY_IS_REMEMBER, cbRemember.isChecked());
        edit.apply();
    }

    /**
     * 跳转到注册页面
     */
    public void toRegister(View view) {
        Intent intent = new Intent(this, com.example.accounts.utils.login.class);
        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == com.example.accounts.utils.login.RESULT_CODE_REGISTER
                && data != null) {
            // 注册成功后自动回填注册的用户名
            String account = data.getStringExtra("account");
            if (!TextUtils.isEmpty(account)) {
                etAccount.setText(account);
                etPassword.setText("");
                etPassword.requestFocus();
            }
        }
    }
}
