package com.sunhang.mobileplayer.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sunhang.mobileplayer.R;
import com.sunhang.mobileplayer.activity.VideoPlayerActivity;
import com.sunhang.mobileplayer.adapter.VideoListAdapter;
import com.sunhang.mobileplayer.bean.VideoItem;
import com.sunhang.mobileplayer.interfaces.Keys;
import com.sunhang.mobileplayer.util.Utils;

import java.util.ArrayList;

/**
 * Created by user on 2016/7/1.
 */

public class VideoListFragment extends BaseFragment {

    private ListView listView;

    @Override
    public void initData() {
        //这种方式查询,会在主线程,容易出问题
//        getActivity().getContentResolver().query()
        AsyncQueryHandler queryHandler = new AsyncQueryHandler(getActivity().getContentResolver()) {
            //这个方法会运行在主线程
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                Utils.printCursor(cursor);
                VideoListAdapter adapter = new VideoListAdapter(getActivity(), cursor);
                listView.setAdapter(adapter);
            }
        };
        int token=0;            //相当于Message.what
        Object cookie=null;     //相当于Message.obj
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection={   //指定要查询哪些列
                MediaStore.Video.Media._ID,//如果数据里面没有_ID 可以用name(AS _ID) 起别名来代替
                MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA
        };
        String selection=null;  //指定查询条件
        String[] selectionArg=null;//指定条件查询中的参数
        String orderBy= MediaStore.Video.Media.TITLE; //排序依据
        //子线程
        queryHandler.startQuery(token,cookie,uri,projection,selection,selectionArg,orderBy);

//        listView.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ArrayList<VideoItem> videoItem = getVideoList(cursor);
                enterVideoPlayerActivity(videoItem, position);
            }
        });
    }

    /**
     * 进入视频播放界面
     * @param videoItem     视频列表
     * @param position      点击视频的位置
     */
    private void enterVideoPlayerActivity(ArrayList<VideoItem> videoItem, int position) {
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra(Keys.ITEM_LIST, videoItem);
        intent.putExtra(Keys.CURRENT_POSITION, position);
        startActivity(intent);
    }

    /**
     * 把cursor所有数据提取出来封装在集合当中
     * @param cursor
     * @return
     */
    private ArrayList<VideoItem> getVideoList(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        cursor.moveToFirst();
        do {
            videoItems.add(VideoItem.fromCursor(cursor));
        } while (cursor.moveToNext());
        return videoItems;
    }

    @Override
    public void initView() {
        listView = (ListView) rootView;
    }

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_media_list;
    }

    @Override
    public void onClick(View v, int id) {

    }
}
