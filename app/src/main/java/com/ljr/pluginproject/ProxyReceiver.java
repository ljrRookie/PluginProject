package com.ljr.pluginproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ljr.base.Constant;
import com.ljr.base.ReceiverInterface;

public class ProxyReceiver extends BroadcastReceiver {
    // 插件里面的 MyReceiver 全类名
    private String pluginMyReceiverClassName;

    public ProxyReceiver(String pluginMyReceiverClassName) {
        this.pluginMyReceiverClassName = pluginMyReceiverClassName;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // 加载插件里面的 MyReceiver
        Log.e(Constant.TAG, "宿主App--ProxyReceiver_onReceive");
        try {
            Class mMyRecevierClass = PluginManager.getInstance(context).getClassLoader().loadClass(pluginMyReceiverClassName);

            // 实例化class
            Object mMyReceiver = mMyRecevierClass.newInstance();

            ReceiverInterface receiverInterface = (ReceiverInterface) mMyReceiver;

            // 执行插件里面的 MyReceiver onReceive方法
            receiverInterface.onReceive(context, intent);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
