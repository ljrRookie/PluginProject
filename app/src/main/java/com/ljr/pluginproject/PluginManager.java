package com.ljr.pluginproject;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class PluginManager {
    private static final String TAG = PluginManager.class.getSimpleName();

    private static PluginManager pluginManager;
    private Context mContext;
    private DexClassLoader mDexClassLoader;
    private Resources mResources;

    public PluginManager(Context context) {
        mContext = context;
    }

    public static PluginManager getInstance(Context context) {
        if (pluginManager == null) {
            synchronized (PluginManager.class) {
                if (pluginManager == null) {
                    pluginManager = new PluginManager(context);
                }
            }
        }
        return pluginManager;
    }

    public ClassLoader getClassLoader() {
        return mDexClassLoader;
    }

    public Resources getResources() {
        return mResources;
    }

    /**
     * 加载插件
     */
    public void loadPlugin() {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "plugin.apk");
            if (!file.exists()) {
                Log.d(TAG, "插件包 不存在...");
                return;
            }
            String pluginPaht = file.getAbsolutePath();
            //下面是加载插件里面的 class

            // dexClassLoader需要一个缓存目录   /data/data/当前应用的包名/pluginDir
            File fileDir = mContext.getDir("pluginDir", Context.MODE_PRIVATE);
            // Activity class
            mDexClassLoader = new DexClassLoader(pluginPaht, fileDir.getAbsolutePath(), null, mContext.getClassLoader());
            //下面是加载插件里面的layout

            //加载资源
            AssetManager assetManager = AssetManager.class.newInstance();

            //我们要执行此方法，为了把插件包的路径 添加进去
            // public final int addAssetPath(String path)
            Method addAssetPathMethod = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, pluginPaht);//插件包的路径

            //宿主的资源配置信息
            Resources resources = mContext.getResources();
            // 特殊的 Resources，加载插件里面的资源的 Resources
            mResources = new Resources(assetManager, resources.getDisplayMetrics(), resources.getConfiguration()); // 参数2 3  资源配置信息

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
