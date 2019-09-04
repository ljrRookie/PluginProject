package com.ljr.plugin_package;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ljr.base.Constant;
import com.ljr.base.ReceiverInterface;

public class TestReceiver extends BroadcastReceiver implements ReceiverInterface {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(Constant.TAG, "插件App--TestReceiver_onReceive");
        Toast.makeText(context, "我是插件里面的广播接收者，我收到广播了", Toast.LENGTH_SHORT).show();
    }
}
