package com.vgtech.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by zhangshaofang on 2015/8/6.
 */
public class PrfUtils {
    private static SharedPreferences mSharePreferences;
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String OPENFIRE = "openfire";
    public static final String IMG_HOST = "IMG_HOST";
    public static final String MBRTYPE = "mbrType";
    public static final String TOKEN = "TOKEN";
    public static final String LOGIN_TICKET = "LOGIN_TICKET";
    public static final String MBRID = "mbrID";

    public static SharedPreferences getSharePreferences(Context context) {
//        if (mSharePreferences == null) {
        mSharePreferences = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_WORLD_READABLE | Context.MODE_MULTI_PROCESS);
//            mSharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        }
        return mSharePreferences;
    }

    public static String getMbrId(Context context) {
        return getPrfparams(context, MBRID);
    }

    public static void savePrfparams(Context context, String key, String value) {
        SharedPreferences preferences = getSharePreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public static String getPrfparams(Context context, String key) {
        SharedPreferences preferences = getSharePreferences(context);
        return preferences.getString(key, null);
    }

    public static void logout(Context context) {
        SharedPreferences preferences = getSharePreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PrfUtils.TOKEN);
        editor.commit();
    }

    public static String getAppLanguage(Context context) {
        String language = PrfUtils.getPrfparams(context, "is_language");
        if (TextUtils.isEmpty(language)) {
            if (context.getResources().getConfiguration().locale.getLanguage().equals("zh")) {
                language = "zh";
            } else {
                language = "en";
            }
        }
        return language;
    }

    public static String getPrfparams(Context context, String key, String defValue) {
        SharedPreferences preferences = getSharePreferences(context);
        return preferences.getString(key, defValue);
    }


    public static String getUserId(Context context) {
        return getMbrId(context);
    }

   /* public static String getServiceHost(Context context) {
        return ApiUtils.getHost(context);
    }*/

    public static String getToken(Context context) {
        final SharedPreferences preferences = getSharePreferences(context);
        return preferences.getString("token", null);
    }

}
