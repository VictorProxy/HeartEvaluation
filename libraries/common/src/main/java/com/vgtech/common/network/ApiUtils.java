package com.vgtech.common.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.vgtech.common.Constants;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.URLAddr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by zhangshaofang on 2015/8/5.
 */
public class ApiUtils {

    private static List<String> ignoreVersionUrl = null;

    /**
     * 以下url与host之间不需要拼接version
     */
    static {
        ignoreVersionUrl = new ArrayList<>();
    }

    private Context mContext;
    private final Random mRandom;

    private Map<String, String> mSignParams;

    public static String serviceHost = null;

    /**
     * 获取登录返回host
     *
     * @param context
     * @return
     */
    public static String getHost(Context context) {
        SharedPreferences preferences = PrfUtils.getSharePreferences(context);
        return preferences.getString("service_host", getLocalHost(context));
    }

    /**
     * 获取本地路径
     *
     * @param context
     * @return
     */
    private static String getLocalHost(Context context) {
        String host = PrfUtils.getPrfparams(context, "host");
        String port = PrfUtils.getPrfparams(context, "port");
        if (!Constants.DEBUG || TextUtils.isEmpty(host) || TextUtils.isEmpty(port)) {
            host = URLAddr.IP;
            port = URLAddr.PORT;
        }
        String scheme = PrfUtils.getPrfparams(context, "scheme", URLAddr.SCHEME);
        String serviceHost = scheme + "://" + host + "/";
        if (!"80".equals(port)) {
            serviceHost = scheme + "://" + host + ":" + port + "/";
        }
        return serviceHost;
    }

    public static String generatorUrl(Context context, String url) {
        serviceHost = getHost(context);
        return appendUrl(context, serviceHost, url, true);
    }

    public static String generatorUrl(Context context, String url, boolean addVersion) {
        serviceHost = getHost(context);
        return appendUrl(context, serviceHost, url, addVersion);
    }

    /**
     * 拼接本地url  无需登录验证
     *
     * @param context
     * @param url
     * @return
     */
    public static String generatorLocalUrl(Context context, String url) {
        return appendUrl(context, getLocalHost(context), url, true);
    }

    private static int mAppVersion = 0;

    private static int getAppVersion(Context context) {
        if (mAppVersion == 0) {
            PackageManager packageManager = context.getPackageManager();
            try {
                PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
                mAppVersion = info.versionCode;
            } catch (Exception e) {
            }
        }
        return mAppVersion;
    }

    public static String addAppVersion(Context context, String path) {
        int version = getAppVersion(context);
        return getHost(context) + "v" + version + "/" + path;
    }

    public static String appendUrl(Context context, String host, String path, boolean addVersion) {
        String url = "";
        if (!addVersion || ignoreVersionUrl.contains(path)) {
            url = host + path;
        } else {
            int version = getAppVersion(context);
            if (path.contains("%")) {
                path = String.format(path, version);
                url = host + path;
            } else {
                url = host + path;
//                url = host + "v" + version + "/" + path;
            }

        }
        return url;
    }

    public ApiUtils(Context context) {
        mContext = context;
        mRandom = new Random();
        mSignParams = new HashMap<String, String>();
        ensureSignInfo();
    }

    public Map<String, String> getSignParams() {
        return mSignParams;
    }


    public void setLanguage(String language) {
        mSignParams.put("language", language);//语言
        mSignParams.put("Lang", language);//语言
    }

    private void ensureSignInfo() {
        TimeZone tz = TimeZone.getDefault();
        String local = tz.getDisplayName(false, TimeZone.SHORT);//时区
//        String language = "zh";
        String language = PrfUtils.getAppLanguage(mContext);
        DisplayMetrics d = mContext.getResources().getDisplayMetrics();
        int screenHeight = d.heightPixels;
        int screenWidth = d.widthPixels;
        int dpi = d.densityDpi;
        String sdk = Build.VERSION.SDK;//操作系统版本
        String model = Build.MODEL;//设备名称
        mSignParams.put("language", language);//语言
        mSignParams.put("Lang", language);//语言
//        mSignParams.put("local", local);//时区
        mSignParams.put("device_model", model);//设备型号
        mSignParams.put("screen_width", String.valueOf(screenWidth));//屏幕宽度
        mSignParams.put("screen_height", String.valueOf(screenHeight));//屏幕高度
        mSignParams.put("dpi", String.valueOf(dpi));//屏幕密度
        mSignParams.put("osver", sdk);//操作系统版本

        PackageManager packageManager = mContext.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            int versionCode = info.versionCode;
            mSignParams.put("vercode", String.valueOf(versionCode));//应用版本号
        } catch (Exception e) {
        }

    }


    public String appendSignInfo(String url) {
//        final String uid = PrfUtils.getUserId(mContext);
//        final String token = PrfUtils.getToken(mContext);
        Uri.Builder builder = Uri.parse(url).buildUpon();
//        if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
//            builder.appendQueryParameter("ownId", String.valueOf(uid)).appendQueryParameter("token", token);
//        }
//        if (TextUtils.isEmpty(Uri.parse(url).getQueryParameter("tenantId"))) {
//            builder.appendQueryParameter("tenantId", getTenantId());
//        }
        //  builder.appendQueryParameter("nonce", String.valueOf(mRandom.nextInt())).appendQueryParameter("timestamp", String.valueOf(System.currentTimeMillis()));
        Iterator<String> keyIterator = mSignParams.keySet().iterator();
        while (keyIterator.hasNext()) {
            String key = (String) keyIterator.next();
            if (TextUtils.isEmpty(Uri.parse(url).getQueryParameter(key))) {
                builder.appendQueryParameter(key, mSignParams.get(key));
            }
        }

        return builder.build().toString();
    }

}
