package com.vgtech.vancloud.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.vgtech.common.PrfUtils;

/**
 * Created by vic on 2017/1/19.
 */
public class LoginPresenter {
    public static boolean isLogin(Context context) {
        SharedPreferences sharedPreferences = PrfUtils.getSharePreferences(context);
        return false;
//        return sharedPreferences.getBoolean(PrfUtils.LOGIN_TICKET, false);
    }
}
