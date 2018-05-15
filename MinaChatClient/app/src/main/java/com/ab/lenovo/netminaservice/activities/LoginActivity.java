package com.ab.lenovo.netminaservice.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.ab.lenovo.netminaservice.MinaService;
import com.ab.lenovo.netminaservice.R;
import com.ab.lenovo.netminaservice.SessionManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText E_userName;
    private EditText E_password;
    private Button loginBtn;
    private Button registerBtn;

    private String userName;
    private String password;

    private MessageBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initView();
        registerBroadcast();

        startSocketService();

    }

    private boolean isNullInformation() {
        userName = E_userName.getText().toString();
        password = E_password.getText().toString();
        if (userName.equals("") || password.equals("")) {
            Toast.makeText(this, "用户名与密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else return false;
    }

    private void startSocketService() {
        Intent intent = new Intent(this, MinaService.class);
        startService(intent);
    }

    private void registerBroadcast() {
        receiver = new MessageBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.ab.lenovo.mina.broadcast");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void initView() {
        E_userName = (EditText) findViewById(R.id.E_userName);
        E_password = (EditText) findViewById(R.id.E_password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn: {
                if (!isNullInformation()) {
                    String loginMessage = "LOGIN:" + userName + ":" + password;
                    SessionManager.getInstance().writeToServer(loginMessage);
                }
            }
            break;
            case R.id.registerBtn:
                if (!isNullInformation()) {
                    String registerMessage = "REGISTER:" + userName + ":" + password;
                    SessionManager.getInstance().writeToServer(registerMessage);
                }
                break;
        }
    }

    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(new Intent(this, MinaService.class));
        unregisterBroadcast();

    }

    public class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("message");
            switch (response) {
                case "LOGIN_SUCCESS":
                    Intent intent1 = new Intent(context, ChatListActivity.class);
                    intent1.putExtra("username", userName);
                    startActivity(intent1);
                    break;
                case "REGISTER_SUCCESS":
                    Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
                    break;
                case "REGISTER_FAILED":
                    Toast.makeText(context, "注册失败", Toast.LENGTH_SHORT).show();
                    break;
                case "LOGIN_FAILED":
                    Toast.makeText(context, "登陆失败", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }
}
