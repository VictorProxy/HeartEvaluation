package com.vgtech.vancloud.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by vic on 2017/1/19.
 */
public class ToastUtils {
    private static Toast toast;

    public static void show(Context context, String tip) {
        if (toast == null) {
            toast = Toast.makeText(context, tip, Toast.LENGTH_SHORT);
        } else {
            toast.setText(tip);
        }
        toast.show();
    }
}
