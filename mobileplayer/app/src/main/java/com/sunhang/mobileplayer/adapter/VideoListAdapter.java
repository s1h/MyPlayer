package com.sunhang.mobileplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.sunhang.mobileplayer.R;
import com.sunhang.mobileplayer.bean.VideoItem;
import com.sunhang.mobileplayer.util.Utils;

/**
 * Created by sh on 16-7-3.
 */
public class VideoListAdapter extends CursorAdapter{
    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    /**
     * 创建一个VIew
     * @param context
     * @param cursor
     * @param parent
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        //创建一个View
        View view = View.inflate(context, R.layout.adapter_media_list, null);

        //用一个叫ViewHolder保存View里面的引用
        ViewHolder holder = new ViewHolder();
        holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
        holder.tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        holder.tv_size = (TextView) view.findViewById(R.id.tv_size);

        //把ViewHolder保存到View中
        view.setTag(holder);
        return view;
    }

    /**
     * 把数据绑定到这个View上 进行显示
     * @param view
     * @param context
     * @param cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //取出ViewHolder
        ViewHolder holder = (ViewHolder) view.getTag();

        //把数据显示到ViewHolder中到控件中
        VideoItem item = VideoItem.fromCursor(cursor);
        holder.tv_title.setText(item.getTitle());
        holder.tv_duration.setText(Utils.formatMillis(item.getDuration()));
        holder.tv_size.setText(Formatter.formatFileSize(context,Long.parseLong(item.getSize())));
    }
    class ViewHolder{
        TextView tv_title;
        TextView tv_duration;
        TextView tv_size;
    }
}
