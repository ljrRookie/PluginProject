package com.ljr.pluginproject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 加载插件
     * @param view
     */
    public void loadPlugin(View view) {
        PluginManager.getInstance(this).loadPlugin();
    }

    /**
     * 启动插件里面的Activity
     * @param view
     */
    public void startPluginActivity(View view) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "plugin.apk");
            if (!file.exists()) {
                Log.d("Plugin", "插件包 不存在...");
                return;
            }
            String path = file.getAbsolutePath();

            // 获取插件包 里面的 Activity
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
            ActivityInfo activityInfo = packageInfo.activities[0];

            // 占位  代理Activity
            Intent intent = new Intent(this, ProxyActivity.class);
            intent.putExtra("className", activityInfo.name);
            startActivity(intent);
        }catch (Exception e){
            Log.e("Plugin", e.getMessage());
            Toast.makeText(this, "插件不存在", Toast.LENGTH_SHORT).show();
        }

    }
}
