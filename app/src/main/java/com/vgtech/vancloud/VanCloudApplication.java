package com.vgtech.vancloud;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.multidex.MultiDexApplication;
import android.view.View;
import android.widget.AdapterView;

import com.activeandroid.ActiveAndroid;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.Volley;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.vgtech.common.network.ApiUtils;
import com.vgtech.common.network.NetworkManager;
import com.vgtech.common.utils.ACache;

import java.io.File;
import java.util.List;


/**
 * Created by zhangshaofang on 2015/7/21.
 */
public class VanCloudApplication extends MultiDexApplication {
    private NetworkManager mNetworkManager;
    private static Context context;

    private static int MAX_MEM = 30 * ByteConstants.MB;

    @Override
    public void onCreate() {
        super.onCreate();
//        init();
        ActiveAndroid.initialize(getApplicationContext());
        initFresco();
//        new AsyncTask<Void, Void, Integer>() {
//            @Override
//            protected Integer doInBackground(Void... params) {
//                ActiveAndroid.initialize(getApplicationContext());
//                initFresco();
//                return 0;
//            }
//
//            @Override
//            protected void onPostExecute(Integer i) {
//
//            }
//        }.execute();
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (ex != null)
                    ex.printStackTrace();
                Intent intent = new Intent(VanCloudApplication.this, com.vgtech.vancloud.ui.MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                sendBroadcast(new Intent("RECEIVER_EXIT"));
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                PendingIntent restartIntent = PendingIntent.getActivity(VanCloudApplication.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                System.gc();
            }
        });
    }

    public void initFresco() {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEM,// 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中图片的最大数量。
                MAX_MEM,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);// 内存缓存中单个图片的最大大小。
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(this);
        configBuilder.setBitmapsConfig(Bitmap.Config.RGB_565);
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return bitmapCacheParams;
            }
        };
        configBuilder.setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams);
        Fresco.initialize(this, configBuilder.build());
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    private ApiUtils mApiUtils;

    public ApiUtils getApiUtils() {
        if (mApiUtils == null) {
            mApiUtils = new ApiUtils(this);
        }
        return mApiUtils;
    }

    public NetworkManager getNetworkManager() {
        if (mNetworkManager == null) {
            ACache aCache = ACache.get(this);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            File cacheDir = new File(this.getCacheDir(), "volley");
            DiskBasedCache cache = new DiskBasedCache(cacheDir);
            requestQueue.start();
            requestQueue.add(new ClearCacheRequest(cache, null));
            mNetworkManager = new NetworkManager(requestQueue, getApiUtils(), aCache);
        }
        return mNetworkManager;
    }


}
