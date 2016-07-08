package com.sunhang.mobileplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by sh on 16-7-3.
 */
public class VideoItem implements Serializable{
    private String title;
    private String duration;
    private String size;
    private String path;

    /**
     * 把一个Cursor对象转换为JavaBean
     * @param cursor
     * @return
     */
    public static VideoItem fromCursor(Cursor cursor) {
        VideoItem item = new VideoItem();
        item.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        item.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        item.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        item.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));

        return item;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
