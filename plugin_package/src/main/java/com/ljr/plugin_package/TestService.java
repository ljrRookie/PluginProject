package com.ljr.plugin_package;

import android.content.Intent;
import android.util.Log;

import com.ljr.base.Constant;

public class TestService extends BaseService {



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(Constant.TAG, "插件App的Service--TestService_onStartCommand");
        // 开启子线程，执行耗时任务
        new Thread(new Runnable(){

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        Log.e(Constant.TAG, "插件里面的服务 正在执行中....");
                    }
                }
            }

        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
