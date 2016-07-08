package com.sunhang.mobileplayer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunhang.mobileplayer.R;
import com.sunhang.mobileplayer.interfaces.UiOperation;
import com.sunhang.mobileplayer.util.Utils;

/**
 * Created by user on 2016/7/1.
 */

public abstract class BaseFragment extends Fragment implements UiOperation {

    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutResID(), null);
        Utils.findButtonSetOnClickListener(rootView,this);
        initView();
        initListener();
        initData();
        return rootView;
    }
    /**
     * 在屏幕中央显示一个Toast
     * @param text
     */
    public void showToast(String text) {
        Utils.showToast(getActivity(), text);
    }
    /**
     * 查找view, 这个方法可以避免强转,让我们省去强转操作
     * @param id
     * @param <T>
     * @return
     */
    public <T> T findView(int id) {
        T view = (T) rootView.findViewById(id);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().finish();//共同的操作
                break;
            default:
                onClick(v,v.getId());
                break;
        }
    }
}
