package com.sunhang.mobileplayer.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.sunhang.mobileplayer.R;
import com.sunhang.mobileplayer.adapter.MainAdapter;
import com.sunhang.mobileplayer.fragment.AudioListFragment;
import com.sunhang.mobileplayer.fragment.VideoListFragment;
import com.sunhang.mobileplayer.util.Utils;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {


    private TextView tv_video;
    private TextView tv_audio;
    private View view_indicator;
    private ViewPager viewPager;
    private int pageSize;
    private int indicatorWidth;

    @Override
    public void initData() {
        changeTitleState(true);
        initViewPager();
        initIndicatorWith();
    }

    /**
     * 初始化指示线的宽
     */
    private void initIndicatorWith() {
        int screenWidth = Utils.getScreenWidth(this);
        indicatorWidth = screenWidth/pageSize;
        view_indicator.getLayoutParams().width = indicatorWidth;
        //让View更新布局参数
        view_indicator.requestLayout();
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new VideoListFragment());
        fragments.add(new AudioListFragment());
        pageSize = fragments.size();
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        tv_video.setOnClickListener(this);
        tv_audio.setOnClickListener(this);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scrollIndicator(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                changeTitleState(position==0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 滚动指示线的方法
     * @param position 屏幕的位置
     * @param positionOffset 滑动屏幕的百分比
     */
    private void scrollIndicator(int position, float positionOffset) {
        float translationX = indicatorWidth * position + indicatorWidth * positionOffset;
        //ViewHelper不显示动画效果, 瞬间与下面同步
        ViewHelper.setTranslationX(view_indicator, translationX);

    }

    @Override
    public void initView() {
        tv_video = findView(R.id.tv_video);
        tv_audio = findView(R.id.tv_audio);
        view_indicator = findView(R.id.view_indicator);
        viewPager = findView(R.id.view_pager);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v, int id) {
        switch (id) {
            case R.id.tv_video:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tv_audio:
                viewPager.setCurrentItem(1);
                break;
            default:

                break;
        }
    }

    /**
     * 改变标题栏状态
     *
     * @param isSelectVideo true 视频  false  音乐
     */
    private void changeTitleState(boolean isSelectVideo) {
        //改变标题颜色状态
        tv_video.setSelected(isSelectVideo);
        tv_audio.setSelected(!isSelectVideo);
        //缩放标题
        scaleTitle(isSelectVideo ? 1.2f : 1.0f,tv_video);
        scaleTitle(!isSelectVideo ? 1.2f : 1.0f,tv_audio);
    }

    /**
     * 缩放标题
     * @param scale 缩放比例
     * @param textView 对哪个view进行缩放
     * @return
     */
    private ViewPropertyAnimator scaleTitle(float scale,TextView textView) {
        return ViewPropertyAnimator.animate(textView).scaleX(scale).scaleY(scale);
    }
}
