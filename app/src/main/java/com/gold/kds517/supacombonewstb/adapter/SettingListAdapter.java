package com.gold.kds517.supacombonewstb.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gold.kds517.supacombonewstb.R;

import java.util.List;

public class SettingListAdapter extends BaseAdapter {
    Context context;
    List<String> datas;
    List<Integer>image_datas;
    LayoutInflater inflater;

    public SettingListAdapter(Context context, List<String> datas,List<Integer>imageDatas) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.datas = datas;
        this.image_datas = imageDatas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_setting_list, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.setting_item);
        textView.setText(datas.get(position));
        ImageView imageView = convertView.findViewById(R.id.setting_image);
        imageView.setBackgroundResource(image_datas.get(position));
        return convertView;
    }
}
