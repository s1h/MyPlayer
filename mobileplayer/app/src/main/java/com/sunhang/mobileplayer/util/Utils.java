package com.sunhang.mobileplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sunhang.mobileplayer.interfaces.Constants;

import java.util.Calendar;

/**
 * Created by user on 2016/7/1.
 */

public class Utils {
    /**
     * 查找Button和ImageButton, 并设置单击监听器
     *
     * @param view
     */
    public static void findButtonSetOnClickListener(View view, OnClickListener listener) {
        //遍历view的所有子view
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (child instanceof Button || child instanceof ImageButton) {
                    child.setOnClickListener(listener);
                } else if (child instanceof ViewGroup) {
                    findButtonSetOnClickListener(child, listener);
                }
            }
        }
    }

    /**
     * 在屏幕中央显示一个Toast
     *
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 获取屏幕宽
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        return screenWidth;
    }

    /**
     * 获取屏幕的高
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        return screenHeight;
    }

    /**
     * 打印Cursor里面所有的记录
     *
     * @param cursor
     */
    public static void printCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        Loger.i(Utils.class,"共有"+cursor.getCount());
        while (cursor.moveToNext()) {
            //遍历所有的列
            for (int i=0; i<cursor.getColumnCount();i++) {
                Loger.i(Utils.class,"----------------------");
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                Loger.i(Utils.class, columnName + "=" + value);
            }
        }
    }

    /**
     * 格式化一个毫秒值, 如果时间大于等于1小时,格式化为01:20:33 小于一个小时格式化为33:33
     * @param duration
     * @return
     */
    public static CharSequence formatMillis(String duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND, Integer.parseInt(duration));
        String pattern = Long.parseLong(duration) / Constants.hourMillis > 0 ? "kk:mm:ss" : "mm:ss";
        return DateFormat.format(pattern, calendar);
    }
}
