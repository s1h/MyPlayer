package com.sunhang.mobileplayer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.sunhang.mobileplayer.R;
import com.sunhang.mobileplayer.interfaces.UiOperation;
import com.sunhang.mobileplayer.util.Utils;

/**
 * Created by user on 2016/6/30.
 * BaseActivity
 */

public abstract class BaseActivity extends FragmentActivity implements UiOperation {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResID());
        //这个可以获取到Activity的根View
        View rootView = findViewById(android.R.id.content);
        Utils.findButtonSetOnClickListener(rootView,this);
        initView();
        initListener();
        initData();

    }

    /**
     * 查找view, 这个方法可以避免强转,让我们省去强转操作
     * @param id
     * @param <T>
     * @return
     */
    public <T> T findView(int id) {
        T view = (T) super.findViewById(id);
        return view;
    }

    /**
     * 在屏幕中央显示一个Toast
     * @param text
     */
    public void showToast(String text) {
        Utils.showToast(this, text);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();//共同的操作
                break;
            default:
                onClick(v,v.getId());
                break;
        }
    }
}
