package com.ljr.plugin_package;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ljr.base.Constant;

public class PluginActivity extends BaseActivity {
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
    }
}
