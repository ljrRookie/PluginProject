package com.ljr.pluginproject;

import android.app.Service;
import android.content.Intent;
import android.nfc.Tag;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ljr.base.Constant;
import com.ljr.base.ServiceInterface;

public class ProxyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(Constant.TAG, "宿主App的Service--ProxyService_onStartCommand");
        String className = intent.getStringExtra("className");
        try {
            Class mTestServiceClass = PluginManager.getInstance(this).getClassLoader().loadClass(className);

            Object mTestService = mTestServiceClass.newInstance();

            ServiceInterface serviceInterface = (ServiceInterface) mTestService;

            // 注入 组件环境
            serviceInterface.insertAppContext(this);

            serviceInterface.onStartCommand(intent, flags, startId);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constant.TAG, "插件化Service不存在");
            Toast.makeText(this,"插件化Service不存在",Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
