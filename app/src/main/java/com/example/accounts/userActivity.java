package com.example.accounts;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

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

import com.example.accounts.utils.login;


public class userActivity extends AppCompatActivity {
    //用户登录界面的Java实现

    private static final String TAG = "userActivity";
    Button btn1;
    private EditText etAccount, etPassword;
    private CheckBox cbRemember;
    // private CheckBox cbAutoLogin;
    public static final int REQUEST_CODE_REGISTER = 2;//注册请求码，

    private String userName = "ccc";//自定义用户名
    private String pass = "1234";//自定义密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
       // btn1 = (Button) findViewById(R.id.btn_login);
        getSupportActionBar().setTitle("登录");
       // btn1.setOnClickListener(this);
        initView();
        initData();
        btn1.setOnClickListener(new View.OnClickListener() {//btn1添加点击事件
            @Override
            public void onClick(View v) {//监听器
                String account = etAccount.getText().toString();//检查输入用户名
                String password = etPassword.getText().toString();//用户输入密码

                Log.d(TAG, "onClick: -------------" + account);
                Log.d(TAG, "password: -------------" + password);

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(userActivity.this, "还没有注册账号！", Toast.LENGTH_LONG).show();
                    return;
                }/*注册账号的判断*/

                  /*检查账号和密码
                  * * 检查账号是否与`userName`匹配。
                  * 如果匹配，再检查密码是否与`pass`（同样，此变量在代码段中未定义）匹配。
  a. 如果账号和密码都匹配，显示登录成功的Toast消息，并根据复选框`cbRemember`和`cbAutoLogin`的状态更新SharedPreferences。
  b. 如果密码不匹配，显示密码错误的Toast消息。
  c. 如果账号不匹配，显示用户名错误的Toast消息。*/
                if (TextUtils.equals(account, userName)) {
                    if (TextUtils.equals(password, pass)) {

                        Toast.makeText(userActivity.this, "恭喜你，登录成功！", Toast.LENGTH_LONG).show();
                        if (cbRemember.isChecked()) {
                            SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                            SharedPreferences.Editor edit = spf.edit();
                            edit.putString("account", account);
                            edit.putString("password", password);
                            edit.putBoolean("isRemember", true);
                            // 自动登录已关闭，始终不保存自动登录状态
                            edit.putBoolean("isLogin", false);
                            edit.apply();

                        } else {//更新SharedPreferences:
                            SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
                            SharedPreferences.Editor edit = spf.edit();
                            edit.putBoolean("isRemember", false);
                            edit.apply();
                        }
                        //启动新活动

                        Intent intent = new Intent(userActivity.this, MainActivity.class);
                        intent.putExtra("account", account);
                        startActivity(intent);
                        userActivity.this.finish();

                    } else {
                        Toast.makeText(userActivity.this, "密码错误！", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(userActivity.this, "用户名错误！", Toast.LENGTH_LONG).show();
                }

            }
        });

        // 自动登录已关闭
        // cbAutoLogin.setOnCheckedChangeListener(...);
        // cbRemember.setOnCheckedChangeListener(...);

    }

    private void initView() {
        btn1 = findViewById(R.id.btn_login);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);
        // cbAutoLogin = findViewById(R.id.cb_auto_login);
    }

    private void initData() {
        SharedPreferences spf = getSharedPreferences("spfRecord", MODE_PRIVATE);
        boolean isRemember = spf.getBoolean("isRemember", false);
        // 自动登录已关闭，不再根据 isLogin 跳转主页
        // boolean isLogin = spf.getBoolean("isLogin", false);
        String account = spf.getString("account", "");
        String password = spf.getString("password", "");

        userName = account;
        pass = password;

        if (isRemember) {
            etAccount.setText(account);
            etPassword.setText(password);
            cbRemember.setChecked(true);
        }
    }


   /*跳转页面注册*/

    public void toRegister(View view) {
        Intent intent = new Intent(this, login.class);

        startActivityForResult(intent, REQUEST_CODE_REGISTER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER && resultCode == login.RESULT_CODE_REGISTER && data != null) {
            Bundle extras = data.getExtras();

            String account = extras.getString("account", "");
            String password = extras.getString("password", "");

            etAccount.setText(account);
            etPassword.setText(password);

            userName = account;
            pass = password;
        }
    }
}



