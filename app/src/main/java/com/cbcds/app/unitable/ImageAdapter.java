package com.cbcds.app.unitable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    Context context;
    int images[];
    LayoutInflater mInflater;

    public ImageAdapter(Context applicationContext, int[] images) {
        context = applicationContext;
        this.images = images;
        mInflater = LayoutInflater.from(applicationContext);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_custom_layout, null);
        }
        ImageView icon = convertView.findViewById(R.id.img_completion_status);
        icon.setImageResource(images[position]);
        return convertView;
    }
}
