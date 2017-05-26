package com.vgtech.vancloud.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.vgtech.vancloud.R;
import com.vgtech.vancloud.VanCloudApplication;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangshaofang on 2015/8/26.
 */
public class Utils {

    public static Context getContext() {
        return VanCloudApplication.getContext();
    }

    public static Resources getResources() {
        return getContext().getResources();
    }

    //从string.xml中获取字符串
    public static String getString(int stringId) {
        //上下文环境获取资源文件夹
        return getResources().getString(stringId);
    }

    //通过资源文件id获取图片对象
    public static Drawable getDrawable(int drawableID) {
        return getResources().getDrawable(drawableID);
    }

    //添加string类型数组的方法
    public static String[] getStringArray(int stringArrayId) {
        return getResources().getStringArray(stringArrayId);
    }

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String DATE_TIME_FORMAT_HOUR = "HH:mm";
    private final static String EMAIL_PATTERN = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$";
    private final static String CARD_PATTERN = "^([0-9]{17}[0-9X]{1})|([0-9]{15})$";


    public static boolean isEmail(String mail) {
        return Pattern.matches(EMAIL_PATTERN, mail);
    }

    public static boolean isCard(String mail) {
        return Pattern.matches(CARD_PATTERN, mail);
    }

    public static String priceFormat(String price) {
        DecimalFormat nf = new DecimalFormat("0.00");
        String s = nf.format(Double.parseDouble(price));
        return s;
    }

    public static String priceFormat01(String price) {
        DecimalFormat nf = new DecimalFormat("#,##0.00");
        String s = nf.format(Double.parseDouble(price));
        return s;
    }

    // dip--px
    public static int convertDipOrPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static String dateFormat(long times) {
        long current = System.currentTimeMillis();
        Date date = new Date(times);
        Date curr = new Date(current);
        String dateformat = DATE_TIME_FORMAT;
        int y = curr.getYear() - date.getYear();
        int m = curr.getMonth() - date.getMonth();
        int t = curr.getDate() - date.getDate();
        if (y == 0 && m == 0) {
            dateformat = "HH:mm";
        }
        SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
        String dateStr = dateformat1.format(date);

        String Str = "";
        if (y == 0 && m == 0) {
            if (t == 0) {
                Str = getString(R.string.today) + " " + dateStr;
            } else if (t == 1) {
                Str = getString(R.string.yesterday) + " " + dateStr;
            } else if (t == 2) {
                Str = getString(R.string.anteayer) + " " + dateStr;
            } else {
                SimpleDateFormat dateformat2 = new SimpleDateFormat(DATE_TIME_FORMAT);
                Str = dateformat2.format(date);
            }
        } else {
            Str = dateStr;
        }

        return Str;
    }


    public static final String VANTOP_DATE_TIME_FORMAT = "yyyy-MM-dd";

    public static String vantopDateFormat(long times) {
        long current = System.currentTimeMillis();
        Date date = new Date(times);
        Date curr = new Date(current);
        String dateformat = VANTOP_DATE_TIME_FORMAT;
        int y = curr.getYear() - date.getYear();
        int m = curr.getMonth() - date.getMonth();
        int t = curr.getDate() - date.getDate();

        SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
        String dateStr = dateformat1.format(date);
        String Str = "";

        if (y == 0 && m == 0) {
            if (t == 0) {
                Str = getString(R.string.today);
            } else if (t == 1) {
                Str = getString(R.string.yesterday);
            } else if (t == 2) {
                Str = getString(R.string.anteayer);
            } else {
                SimpleDateFormat dateformat2 = new SimpleDateFormat(VANTOP_DATE_TIME_FORMAT);
                Str = dateformat2.format(date);
            }
        } else {
            Str = dateStr;
        }

        return Str;
    }

    /**
     * @param lo 毫秒数
     * @return String yyyy-MM-dd HH:mm:ss
     * @Description: long类型转换成日期
     */
    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sd.format(date);
    }
//
//    /**
//     * cache dir
//     * @param context
//     * @return
//     */
//    public static String getTempDirectoryPath(Context context) {
//        return context.getExternalFilesDir(null).getAbsolutePath();
//    }

    public static String dateFormatStr(long times) {
        Date date = new Date(times);
        String dateformat = DATE_TIME_FORMAT;
        SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
        String dateStr = dateformat1.format(date);
        return dateStr;
    }

    public static String dateFormatDate(long times) {
        Date date = new Date(times);
        String dateformat = "yyyy-MM-dd";
        SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
        String dateStr = dateformat1.format(date);
        return dateStr;
    }

    public static String dateFormatToDate(long times) {
        Date date = new Date(times);
        String dateformat = DATE_FORMAT;
        SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
        String dateStr = dateformat1.format(date);
        return dateStr;
    }

    public static String dateFormatHour(long times) {
        Date date = new Date(times);
        String dateformat = DATE_TIME_FORMAT_HOUR;
        SimpleDateFormat dateformat1 = new SimpleDateFormat(dateformat);
        String dateStr = dateformat1.format(date);
        return dateStr;
    }

    /**
     * 时间格式化
     *
     * @param times
     * @return
     */
    public static long dateFormat(String times) {
        DateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date date = null;
        try {
            date = sdf.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static long dateFormat(String times, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static String dateFormat(long times, String format) {
        Date date = new Date(times);
        SimpleDateFormat dateformat1 = new SimpleDateFormat(format);
        String dateStr = dateformat1.format(date);
        return dateStr;
    }

    public static int convertDipOrPx(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    public static String[] getIds(String idstr) {
        String[] ids = null;
        if (!TextUtils.isEmpty(idstr)) {
            if (idstr.contains(",")) {
                ids = idstr.split("[,]");
            } else {
                ids = new String[]{idstr};
            }
        }
        return ids;
    }

    public static String dateFormatByString(String times) {

        String newTime = "";
        long time = 0;
        if (!TextUtils.isEmpty(times)) {
            time = Long.valueOf(times);
        }
        if (time > 0) {
            newTime = dateFormat(time);
        }
        return newTime;
    }

    public static String getDate(String times, SimpleDateFormat format) {
        Date date = null;
        try {
            date = format.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime() + "";
    }




    public static boolean isPhoneNum(String value) {
        String regex = "^((13[0-9])|(15[0-9])|(17[0-9])|(14[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        return m.find();
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


    public static Bitmap imageZoom(Bitmap bitMap) {
        if (bitMap == null)
            return null;
        //图片允许最大空间   单位：KB
        double maxSize = 32.00;
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        //将字节换成KB
        double mid = b.length / 1024;
        //判断bitmap占用空间是否大于允许最大空间  如果大于则压缩 小于则不压缩
        if (mid > maxSize) {
            //获取bitmap大小 是允许最大大小的多少倍
            double i = mid / maxSize;
            //开始压缩  此处用到平方根 将宽带和高度压缩掉对应的平方根倍 （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
            double lengthSide = bitMap.getWidth() > bitMap.getHeight() ? bitMap.getHeight() : bitMap.getWidth();
            bitMap = zoomImage(bitMap, lengthSide / Math.sqrt(i),
                    lengthSide / Math.sqrt(i));
        }
        return bitMap;
    }


    /**
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) newWidth,
                (int) newHeight, matrix, true);
        return bitmap;
    }


    public static String format(String format, String str) {
        if (TextUtils.isEmpty(str))
            str = "";
        return String.format(format, str);
    }

    /**
     * 官方dp转px
     *
     * @param resources
     * @param dps
     * @return
     */
    public static int sysDpToPx(Resources resources, int dps) {
        return Math.round(resources.getDisplayMetrics().density * (float) dps);
    }
}