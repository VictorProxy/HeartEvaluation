package com.vgtech.vancloud.utils;

import android.content.Context;
import android.widget.Toast;

import com.vgtech.common.api.RootData;

/**
 * Created by vic on 2017/1/19.
 */
public class ActivityUtils {
    public static boolean prehandleNetworkData(Context context, RootData rootData) {

        boolean result = rootData.isSuccess();
        if (!result) {
            if (!NetworkHelpers.isNetworkAvailable(context)) {
                ToastUtils.show(context, "无网络链接");
            } else {
              String msg =   rootData.getMsg();
                ToastUtils.show(context, msg);
            }
        }
        return result;

    }
}
