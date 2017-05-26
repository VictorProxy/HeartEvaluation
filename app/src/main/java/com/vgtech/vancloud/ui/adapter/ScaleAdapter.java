package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Scale;

/**
 * Created by vic on 2017/3/8.
 */
public class ScaleAdapter extends BaseSimpleAdapter<Scale> {
    public ScaleAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.cp_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Scale scale = getItem(position);
        SimpleDraweeView user_photo = holder.getView(R.id.cp_img);
        TextView name = holder.getView(R.id.cp_name);
        TextView desc = holder.getView(R.id.cp_desc);
        TextView comment_count = holder.getView(R.id.comment_count);
        ImageOptions.setImage(user_photo, scale.picPath);
        name.setText(scale.name);
        desc.setText(scale.memo);
        comment_count.setText(scale.useTimes);
        return convertView;
    }
}
