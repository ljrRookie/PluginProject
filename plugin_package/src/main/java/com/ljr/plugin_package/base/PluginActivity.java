package com.ljr.plugin_package.base;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ljr.base.Constant;
import com.ljr.plugin_package.R;
import com.ljr.plugin_package.TestActivity;
import com.ljr.plugin_package.TestReceiver;
import com.ljr.plugin_package.TestService;
import com.ljr.plugin_package.base.BaseActivity;

public class PluginActivity extends BaseActivity {
    private final String ACTION = "com.ljr.plugin_package.ACTION";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin_main);
        Log.e(Constant.TAG, "插件App--PluginActivity_onCreate");
        // this 会报错，因为插件没有安装，也没有组件的环境，所以必须使用宿主环境
        Toast.makeText(appActivity, "我是插件", Toast.LENGTH_SHORT).show();
        findViewById(R.id.bt_start_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(appActivity, TestActivity.class));
            }
        });
        findViewById(R.id.bt_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(appActivity, TestService.class));
            }
        });
        // 注册广播
        findViewById(R.id.bt_register_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION);
                registerReceiver(new TestReceiver(), intentFilter);
            }
        });
        // 发送广播
        findViewById(R.id.bt_send_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(ACTION);
                sendBroadcast(intent);
            }
        });
    }
}
