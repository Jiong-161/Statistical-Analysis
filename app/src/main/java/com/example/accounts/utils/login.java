package com.example.accounts.utils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accounts.R;
import com.example.accounts.db.DBManager;

/**
 * 注册页面
 * <p>
 * 注册流程：前端校验（用户名长度、密码强度、两次密码一致）
 * → 查重（isUsernameExists）→ 密码加盐哈希（DBManager.registerUser内部处理）
 * → 写入数据库 → 返回结果
 * </p>
 */
public class login extends AppCompatActivity implements View.OnClickListener {

    public static final int RESULT_CODE_REGISTER = 2;
    private Button btnRegister;
    /**
     * 用户名长度限制
     */
    private static final int USERNAME_MIN_LEN = 4;
    private CheckBox cbAgree;
    private static final int USERNAME_MAX_LEN = 20;
    /**
     * 密码最小长度
     */
    private static final int PASSWORD_MIN_LEN = 6;
    private EditText etAccount, etPass, etPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("注册");

        etAccount = findViewById(R.id.et_account);
        etPass = findViewById(R.id.et_password);
        etPassConfirm = findViewById(R.id.et_password_confirm);
        cbAgree = findViewById(R.id.cb_agree);
        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        performRegister();
    }

    /**
     * 执行注册操作
     * 流程：前端校验 → 查重 → 写入数据库 → 结果反馈
     */
    private void performRegister() {
        // 1. 获取输入并去除首尾空格
        String name = etAccount.getText().toString().trim();
        String pass = etPass.getText().toString();
        String passConfirm = etPassConfirm.getText().toString();

        // 2. 用户名校验：非空 + 长度限制
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(login.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() < USERNAME_MIN_LEN || name.length() > USERNAME_MAX_LEN) {
            Toast.makeText(login.this,
                    "用户名需" + USERNAME_MIN_LEN + "-" + USERNAME_MAX_LEN + "个字符",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 用户名字符集限制（仅允许字母、数字、下划线、中文）
        if (!name.matches("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) {
            Toast.makeText(login.this, "用户名只能包含字母、数字、下划线或中文", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 密码校验：非空 + 最小长度
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(login.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < PASSWORD_MIN_LEN) {
            Toast.makeText(login.this, "密码至少" + PASSWORD_MIN_LEN + "位", Toast.LENGTH_SHORT).show();
            return;
        }

        // 4. 两次密码一致性校验
        if (!TextUtils.equals(pass, passConfirm)) {
            Toast.makeText(login.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();
            return;
        }

        // 5. 协议勾选校验
        if (!cbAgree.isChecked()) {
            Toast.makeText(login.this, "请同意用户协议", Toast.LENGTH_LONG).show();
            return;
        }

        // 6. 查重：检查用户名是否已注册
        if (DBManager.isUsernameExists(name)) {
            Toast.makeText(login.this, "该用户名已被注册，请更换", Toast.LENGTH_LONG).show();
            return;
        }

        // 7. 调用DBManager写入数据库（内部自动完成盐值生成+哈希计算）
        boolean success = DBManager.registerUser(name, pass);

        if (success) {
            Toast.makeText(login.this, "注册成功！", Toast.LENGTH_SHORT).show();

            // 数据回传给登录页（仅回传账号名，不回传密码明文）
            Intent intent = new Intent();
            intent.putExtra("account", name);
            setResult(RESULT_CODE_REGISTER, intent);

            this.finish();
        } else {
            Toast.makeText(login.this, "注册失败，请稍后重试", Toast.LENGTH_LONG).show();
        }
    }
}
