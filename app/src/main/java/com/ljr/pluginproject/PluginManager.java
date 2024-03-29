package com.ljr.pluginproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ljr.base.Constant;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

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

    /**
     * 反射系统源码，来解析apk文件里面的所有信息
     */
    public void parserApkAction(){
        try {
            //插件包路径
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "plugin.apk");
            if(!file.exists()){
                Log.e(Constant.TAG, "插件包不存在");
                return;
            }
            //实例化 PackageParser对象
            Class mPackageParserClass = Class.forName("android.content.pm.PackageParser");
            Object mPackageParser = mPackageParserClass.newInstance();

            //1.执行此方法 public Package parsePackage(File packageFile,int flags),就是为了，拿到Package
            Method mPackageParserMethod = mPackageParserClass.getMethod("parsePackage", File.class, int.class);
            Object mPackage = mPackageParserMethod.invoke(mPackageParser, file, PackageManager.GET_ACTIVITIES);

            //继续分析 Package
            //得到 receivers
            Field receiversField = mPackage.getClass().getDeclaredField("receivers");
            //receivers 本质就是 ArrayList 集合
            Object receivers = receiversField.get(mPackage);
            ArrayList arrayList = (ArrayList) receivers;
            // 此Activity 不是组件的Activity，是PackageParser里面的内部类
            for (Object mActivity : arrayList) { // mActivity --> <receiver android:name=".StaticReceiver">
            //获取<intent-filter> intents == 很多 Intent-Filter
            //通过反射拿到 intents
                Class mComponentClass = Class.forName("android.content.pm.PackageParser$Component");
                Field intentsField = mComponentClass.getDeclaredField("intents");
                // intents 所属的对象是谁 ?
                ArrayList<IntentFilter> intents = (ArrayList) intentsField.get(mActivity);

                // 我们还有一个任务，就是要拿到 android:name=".StaticReceiver"
                // activityInfo.name; == android:name=".StaticReceiver"
                // 分析源码 如何 拿到 ActivityInfo

                Class mPackageUserState = Class.forName("android.content.pm.PackageUserState");
                Class mUserHandle = Class.forName("android.os.UserHandle");
                int userId = (int) mUserHandle.getMethod("getCallingUserId").invoke(null);

                /**
                 * 执行此方法，就能拿到 ActivityInfo
                 * public static final ActivityInfo generateActivityInfo(Activity a, int flags,
                 *             PackageUserState state, int userId)
                 */
                Method generateActivityInfoMethod = mPackageParserClass.getDeclaredMethod("generateActivityInfo", mActivity.getClass(),
                        int.class, mPackageUserState, int.class);
                generateActivityInfoMethod.setAccessible(true);
                //执行此方法，拿到ActivityInfo
                ActivityInfo mActivityInfo = (ActivityInfo) generateActivityInfoMethod.invoke(null,mActivity, 0, mPackageUserState.newInstance(), userId);
                String receiverClassName = mActivityInfo.name; // com.ljr.plugin_package.StaticReceiver
                mDexClassLoader = (DexClassLoader) getClassLoader();
                if(mDexClassLoader==null){
                    loadPlugin();
                }
                Class mStaticReceiverClass =mDexClassLoader .loadClass(receiverClassName);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) mStaticReceiverClass.newInstance();
                for (IntentFilter intentFilter : intents) {
                    // 注册广播
                    mContext.registerReceiver(broadcastReceiver, intentFilter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "加载插件包内的静态广播失败", Toast.LENGTH_SHORT).show();
        }
    }
}
