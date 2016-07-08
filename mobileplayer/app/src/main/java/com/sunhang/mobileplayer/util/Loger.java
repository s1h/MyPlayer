package com.sunhang.mobileplayer.util;

import android.util.Log;

/**
 * Created by sxh on 2016/7/1.
 */
public class Loger {
    /**
     * 控制Log输出的
     *
     * @return
     */
    public static boolean isShowLog = true;

    public static void i(Object objTag, String msg) {
        if (!isShowLog) {
            return;
        }
        String tag;
        //如果是String, 直接使用
        //      如果不是String,则使用他的类名
        if (objTag instanceof String) {
            tag = (String) objTag;
        } else if (objTag instanceof Class) {
            tag = ((Class) objTag).getSimpleName();
        } else {
            tag = objTag.getClass().getSimpleName();
        }
        Log.i(tag, msg);
    }
}
