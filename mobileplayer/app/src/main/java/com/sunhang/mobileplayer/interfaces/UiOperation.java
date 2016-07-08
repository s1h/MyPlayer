package com.sunhang.mobileplayer.interfaces;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by user on 2016/7/1.
 * Ui操作接口
 */

public interface UiOperation extends OnClickListener {
    /**
     *初始化数据, 并显示在界面上
     */
    void initData();

    /**
     *初始化监听器
     */
    void initListener();

    /**
     *初始化View
     */
    void initView();

    /**
     *
     * @return 返回一个用于设置界面的布局ID
     */
    int getLayoutResID();

    /**
     * 单击事件在这个方法中处理
     * @param v
     * @param id
     */
    void onClick(View v, int id);


}
