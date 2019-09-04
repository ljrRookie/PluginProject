package com.ljr.plugin_package;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ljr.base.Constant;

public class TestActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);
        Log.e(Constant.TAG, "插件App的Activity--TestActivity_onCreate");
    }
}
