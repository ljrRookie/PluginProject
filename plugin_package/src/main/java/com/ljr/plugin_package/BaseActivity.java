package com.ljr.plugin_package;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ljr.base.ActivityInterface;

public class BaseActivity extends Activity implements ActivityInterface {
    public Activity appActivity; // 宿主的环境
    @Override
    public void insertAppContext(Activity appActivity) {
        this.appActivity = appActivity;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onStart() {

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onResume() {

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {

    }
    public void setContentView(int resId) {
        appActivity.setContentView(resId);
    }

    public View findViewById(int layoutId) {
        return appActivity.findViewById(layoutId);
    }

    @Override
    public void startActivity(Intent intent) {

        Intent intentNew = new Intent();
        intentNew.putExtra("className", intent.getComponent().getClassName());
        appActivity.startActivity(intentNew);
    }

    @Override
    public ComponentName startService(Intent service) {
        Intent intentNew = new Intent();
        intentNew.putExtra("className", service.getComponent().getClassName()); // TestService 全类名
        return appActivity.startService(intentNew);
    }
}
