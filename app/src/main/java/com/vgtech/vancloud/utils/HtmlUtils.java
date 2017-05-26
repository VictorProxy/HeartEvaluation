package com.vgtech.vancloud.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.TextView;

import com.vgtech.common.PrfUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by vic on 2017/5/23.
 */
public class HtmlUtils {

    private static String imgHost;

    public static String getImgSrc(Context context,String ori) {
        if (TextUtils.isEmpty(imgHost)) {
            imgHost = PrfUtils.getPrfparams(context, PrfUtils.IMG_HOST);
        }
        String converStr = ori;
        if (ori.contains("<IMG") && ori.endsWith("</IMG>") || ori.contains("<img") && ori.endsWith("</img>")) {
            converStr = ori.replace("../", imgHost).replace("<IMG", "<img").replace("</IMG>", "</img>");
        }
        int start = converStr.indexOf("http");
        int end = start+converStr.substring(start).indexOf("\">");
        return converStr.substring(start, end);
    }

    public static boolean isImg(String ori) {
        if (TextUtils.isEmpty(ori))
            return false;
        if (ori.contains("<IMG") && ori.endsWith("</IMG>") || ori.contains("<img") && ori.endsWith("</img>")) {
            return true;
        }
        return false;
    }
}
