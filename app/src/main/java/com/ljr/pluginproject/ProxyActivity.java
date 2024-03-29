package com.ljr.pluginproject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ljr.base.ActivityInterface;
import com.ljr.base.Constant;

import java.lang.reflect.Constructor;

public class ProxyActivity extends Activity {
    @Override
    public Resources getResources() {
        return PluginManager.getInstance(this).getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance(this).getClassLoader();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(Constant.TAG, "宿主App--ProxyActivity_onCreate");
        //真正的加载 插件里面的Activity
        String className = getIntent().getStringExtra("className");
        try {
            Class mPluginActivityClass = getClassLoader().loadClass(className);
            //实例化 插件包里面的Activity
            Constructor constructor = mPluginActivityClass.getConstructor(new Class[]{});
            Object mPluginActivity = constructor.newInstance(new Object[]{});
            ActivityInterface activityInterface = (ActivityInterface)mPluginActivity;
            // 注入
            activityInterface.insertAppContext(this);
            Bundle bundle = new Bundle();
            bundle.putString("appName", "我是宿主传递过来的信息");

            // 执行插件里面的onCreate方法
            activityInterface.onCreate(bundle);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"启动插件化Activity失败",Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void startActivity(Intent intent) {
        String className = intent.getStringExtra("className");

        Intent proxyIntent = new Intent(this, ProxyActivity.class);
        proxyIntent.putExtra("className", className); // 包名+TestActivity

        // 要给TestActivity 进栈
        super.startActivity(proxyIntent);
    }

    @Override
    public ComponentName startService(Intent service) {
        Log.e(Constant.TAG, "ProxyActivity_startService");
        String className = service.getStringExtra("className");

        Intent intent = new Intent(this, ProxyService.class);
        intent.putExtra("className", className);
        return super.startService(intent);
    }
}
