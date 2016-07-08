package com.sunhang.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.sunhang.mobileplayer.R;
import com.sunhang.mobileplayer.bean.VideoItem;
import com.sunhang.mobileplayer.interfaces.Keys;
import com.sunhang.mobileplayer.util.Utils;
import com.sunhang.mobileplayer.view.VideoView;

import java.util.ArrayList;

/**
 * Created by sh on 16-7-3.
 */
public class VideoPlayerActivity extends BaseActivity{
    private static final int UPDATE_CURRENT_POSITION = 1;
    private static final int HIDE_CTRL_LAYOUT = 2;
    private VideoView videoView;
    private ArrayList<VideoItem> videoItems;
    private int currentPosition;
    private VideoItem currentVideoItem;
    private LinearLayout ll_top_ctrl;
    private LinearLayout ll_bottom_ctrl;
    private TextView tv_title;
    private TextView tv_system_time;
    private TextView tv_current_position;
    private TextView tv_duration_time;
    private Button btn_pre;
    private Button btn_play;
    private Button btn_next;
    private Button btn_fullscreen;
    private SeekBar sb_voice;
    private SeekBar sb_video;
    private ImageView iv_battery;
    private float currentAlpha;
    private static final int UPDATE_SYS_TIME = 0;
    private GestureDetector gestureDetector;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_SYS_TIME:
                    showSysTime();
                    break;
                case UPDATE_CURRENT_POSITION:
                    updateCurrentPosition();
                    break;
                case HIDE_CTRL_LAYOUT:
                    hideCtrlLayoutToggle();
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver batteryChangeReceiver;
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private int currentVolume1;
    private boolean isDownLeft;
    private View view_brightness;

    @Override
    public void initData() {
        videoItems = (ArrayList<VideoItem>) getIntent().getSerializableExtra(Keys.ITEM_LIST);
        currentPosition = getIntent().getIntExtra(Keys.CURRENT_POSITION, -1);
        Uri videoUri = getIntent().getData();
        if (videoUri != null) {
            //说明是从第三方跳转过来打
            videoView.setVideoURI(videoUri);
            btn_pre.setEnabled(false);
            btn_next.setEnabled(false);
        } else {
            //从视频列表

        }
        openVideo();

    }

    /**
     * 打开一个视频
     */
    private void openVideo() {
        if (videoItems == null || videoItems.isEmpty() || currentPosition == -1) {
            return;
        }
        btn_pre.setEnabled(currentPosition !=0);
        btn_next.setEnabled(currentPosition != (videoItems.size()-1));
        currentVideoItem = videoItems.get(currentPosition);
        videoView.setVideoPath(currentVideoItem.getPath());
    }

    @Override
    public void initListener() {
        videoView.setOnPreparedListener(mOnPreparedListener);
        sb_voice.setOnSeekBarChangeListener(voiceSeekBarChangeListener);

        sb_video.setOnSeekBarChangeListener(videoSeekBarChangeListener);
        gestureDetector = new GestureDetector(simpleOnGestureListener);
    }

    public GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){



        //双击
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playToggle();
            return true;
        }
        //按下
        @Override
        public boolean onDown(MotionEvent e) {
            currentVolume = getVolume();
            currentAlpha = view_brightness.getAlpha();
            isDownLeft = e.getX() < (Utils.getScreenWidth(getApplicationContext()) /2);
            return super.onDown(e);
        }
        //长按
        @Override
        public void onLongPress(MotionEvent e) {
            fullScreenToggle();
        }
        //滑动
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // 计算在屏幕垂直方向滑动的距离是多少
            float distance = e1.getY() - e2.getY();
            if (isDownLeft) {
                //改变屏幕亮度
                changeBrightnessByDistance(-distance);
            } else {
                //改变屏幕音量
                changeVolumeByDistance(distance);
            }
            return true;
        }
        //单击
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            hideCtrlLayoutToggle();
            return true;
        }
    };

    /**
     * 隐藏控制面板打开关
     */
    private void hideCtrlLayoutToggle() {
        //原来y方向移动打距离
        float translationY = ViewHelper.getTranslationY(ll_top_ctrl);
        if (translationY == 0) {
            //原来控制面板是显示
            ViewPropertyAnimator.animate(ll_top_ctrl).translationY(-ll_top_ctrl.getHeight());
            ViewPropertyAnimator.animate(ll_bottom_ctrl).translationY(ll_bottom_ctrl.getHeight());
        } else {
            //原来是隐藏的
            ViewPropertyAnimator.animate(ll_top_ctrl).translationY(0);
            ViewPropertyAnimator.animate(ll_bottom_ctrl).translationY(0);
            sendHideCtrlLayoutMsg();
        }
    }
    /**发送隐藏面板的消息 */
    private void sendHideCtrlLayoutMsg() {
        removeSendHideCtrlLayoutMsg();
        handler.sendEmptyMessageDelayed(HIDE_CTRL_LAYOUT, 5000);
    }
    /**移除隐藏面板的消息 */
    private void removeSendHideCtrlLayoutMsg(){
        handler.removeMessages(HIDE_CTRL_LAYOUT);
    }

    private void playToggle() {
        if (videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }
        updatePlayButtonBg();
    }

    /**
     * 更新播放按钮背景
     */
    private void updatePlayButtonBg() {
        int resId;
        if (videoView.isPlaying()) {
            resId = R.drawable.selector_pause;
        } else {
            resId = R.drawable.selector_play;
        }
        btn_play.setBackgroundResource(resId);

    }

    /**
     * 通过滑动的距离来改变音量
     * @param distance 滑动距离
     */
    private void changeVolumeByDistance(float distance) {
        //计算音量最大值和屏幕高的最大值的比例
        //最后要算的是什么值,则这个值对应的单位作为被除数
        float scale = (float)maxVolume / Utils.getScreenHeight(getApplicationContext());

        //计算滑动距离对应的音量值
        float moveValume = distance * scale;

        //在原来音量的基础上再加移动对应的音量值
        int resultVolume = (int) (currentVolume + moveValume);
        //预防超出音量的范围
        if (resultVolume < 0) {
            resultVolume = 0;
        } else if (resultVolume > maxVolume) {
            resultVolume=maxVolume;
        }
        setVolume(resultVolume);
        sb_voice.setProgress(resultVolume);
    }/**
     * 通过滑动的距离来改变亮度
     * @param distance 滑动距离
     */
    private void changeBrightnessByDistance(float distance) {
        //计算亮度最大值和屏幕高的最大值的比例
        //最后要算的是什么值,则这个值对应的单位作为被除数
        float scale = 1.0f / Utils.getScreenHeight(getApplicationContext());

        //计算滑动距离对应的亮度
        float moveValue= distance * scale;

        //在原来连读值的基础上再加移动对应的亮度值
        float resultVolue =   currentAlpha + moveValue;
        //预防超出亮度的范围
        if (resultVolue < 0) {
            resultVolue = 0;
        } else if (resultVolue > 0.9f) {
            resultVolue=0.9f;
        }
        view_brightness.setAlpha(resultVolue);
    }

    /**
     * 视频SeekBar改变的监听器
     */
    SeekBar.OnSeekBarChangeListener videoSeekBarChangeListener= new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeSendHideCtrlLayoutMsg();
            setVolume(0);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideCtrlLayoutMsg();
            setVolume(currentVolume);
        }
    };
    /**
     * 音量改变监听器
     */
    SeekBar.OnSeekBarChangeListener voiceSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {//判断是否是用户触发的
                setVolume(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            removeSendHideCtrlLayoutMsg();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendHideCtrlLayoutMsg();
        }
    };
    @Override
    public void initView() {
        videoView = findView(R.id.video_view);
        ll_top_ctrl = findView(R.id.ll_top_ctrl);
        ll_bottom_ctrl = findView(R.id.ll_bottom_ctrl);
        tv_title = findView(R.id.tv_title);
        tv_system_time = findView(R.id.tv_system_time);
        tv_current_position = findView(R.id.tv_current_position);
        tv_duration_time = findView(R.id.tv_duration_time);
        btn_pre = findView(R.id.btn_pre);
        btn_play = findView(R.id.btn_play);
        btn_next = findView(R.id.btn_next);
        btn_fullscreen = findView(R.id.btn_fullscreen);
        sb_voice = findView(R.id.sb_voice);
        sb_video = findView(R.id.sb_video);
        iv_battery = findView(R.id.iv_battery);
        view_brightness = findView(R.id.view_brightness);
        view_brightness.setAlpha(0);//透明度
        view_brightness.setVisibility(View.VISIBLE);
        showSysTime();

        registerBatteryChangeReceiver();

        initVolume();
        hideCtrlLayout();
    }

    /**
     * 隐藏控制面板
     */
    private void hideCtrlLayout() {
//        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        //上面返回打就是两个0
        ll_top_ctrl.measure(0, 0);//用未指定打模式去测量这个容器
        //隐藏顶部的,应该是负数
        ViewHelper.setTranslationY(ll_top_ctrl, -ll_top_ctrl.getMeasuredHeight());
        ll_bottom_ctrl.measure(0, 0);
        ViewHelper.setTranslationY(ll_bottom_ctrl, ll_bottom_ctrl.getMeasuredHeight());
    }


    @Override
    public int getLayoutResID() {
        return R.layout.activity_video_player;
    }

    @Override
    public void onClick(View v, int id) {
        removeSendHideCtrlLayoutMsg();
        switch (id) {
            case R.id.btn_voice:
                muteToggle();
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_play:
                playToggle();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_fullscreen:
                fullScreenToggle();
                break;
        }
        sendHideCtrlLayoutMsg();
    }

    private void fullScreenToggle() {
        videoView.fullscreenToggle();
        updateFsBtnBg();
    }

    /**
     * 更新全屏按钮打背景
     */
    private void updateFsBtnBg() {
        if (videoView.isFullscreen()) {
            btn_fullscreen.setBackgroundResource(R.drawable.selector_defaultscreen);
        } else {
            btn_fullscreen.setBackgroundResource(R.drawable.selector_fullscreen);
        }
    }

    private void next() {
        if (videoItems != null && currentPosition != (videoItems.size()-1)) {
            currentPosition ++;
            openVideo();
        }
    }

    /**
     * 播放上一个视频
     */
    private void pre() {
        if (currentPosition != 0) {
            currentPosition--;
            openVideo();
        }
    }

    /**
     * 静音开关
     */
    private void muteToggle() {
        //先取出当前音量,如果有音量值,则先把音量值保存,再设置为0,如果当前值为0,则恢复
        if (getVolume() > 0) {
            currentVolume1 = getVolume();
            setVolume(0);
            sb_voice.setProgress(0);
        } else {
            setVolume(currentVolume1);
            sb_voice.setProgress(currentVolume1);
        }
    }

    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();
            videoView.canPause();
            if (currentVideoItem != null) {
                tv_title.setText(currentVideoItem.getTitle());
            } else {
                //说明从第三方调用
                tv_title.setText(getIntent().getData().getPath());
            }
            tv_duration_time.setText(Utils.formatMillis(String.valueOf(videoView.getDuration())));
            sb_video.setMax(videoView.getDuration());
            updateCurrentPosition();
            updatePlayButtonBg();
            updateFsBtnBg();
        }
    };

    /**
     * 更新当前播放位置
     */
    private void updateCurrentPosition() {
        int currentPosition = videoView.getCurrentPosition();
        tv_current_position.setText(Utils.formatMillis(String.valueOf(videoView.getCurrentPosition())));
        sb_video.setProgress(currentPosition);
        handler.sendEmptyMessageDelayed(UPDATE_CURRENT_POSITION, 1000);
    }

    private void showSysTime() {
        tv_system_time.setText(DateFormat.format("kk:mm:ss",System.currentTimeMillis()));
        handler.sendEmptyMessageDelayed(UPDATE_SYS_TIME, 1000);
    }

    /**
     * 注册电量改变的接受者
     */
    private void registerBatteryChangeReceiver() {
        batteryChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //电量范围0-100
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                updateBatteryBg(level);
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryChangeReceiver, filter);
    }

    /**
     * 更新系统电量背景
     * @param level 电量值 0-100
     */
    private void updateBatteryBg(int level) {
        int resid;
        if (level > 95) {
            resid = R.drawable.ic_battery_100;
        } else if (level > 80) {
            resid = R.drawable.ic_battery_80;
        } else if (level > 60) {
            resid = R.drawable.ic_battery_60;
        } else if (level > 40) {
            resid = R.drawable.ic_battery_40;
        } else if (level > 20) {
            resid = R.drawable.ic_battery_20;
        } else if (level > 10) {
            resid = R.drawable.ic_battery_10;
        } else{
            resid = R.drawable.ic_battery_0;
        }
        iv_battery.setBackgroundResource(resid);
    }

    /**
     * 初始化音量
     */
    private void initVolume() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volumeValue;
        currentVolume = getVolume();
        sb_voice.setMax(maxVolume);
        sb_voice.setProgress(currentVolume);
    }

    /**
     * 设置音量值
     * @param volumeValue 设置的音量值
     */
    private void setVolume(int volumeValue) {
        int flags =0; //0-不显示系统电量浮动面板,1-就显示
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volumeValue,flags);
    }

    /**
     * 获取当前的音量值
     * @return  当前音量值
     */
    private int getVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 手势操作
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = gestureDetector.onTouchEvent(event);
        //手势监听器只能识别按下
                //在这里可以识别按下抬起
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                removeSendHideCtrlLayoutMsg();
                break;
            case MotionEvent.ACTION_UP:
                sendHideCtrlLayoutMsg();
                break;
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (batteryChangeReceiver != null) {
            unregisterReceiver(batteryChangeReceiver);
        }
    }
}
