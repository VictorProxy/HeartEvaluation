package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Record;
import com.vgtech.vancloud.api.Scale;
import com.vgtech.vancloud.utils.Utils;

/**
 * Created by vic on 2017/3/8.
 */
public class RecordAdapter extends BaseSimpleAdapter<Record> {
    public RecordAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.record_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Record scale = getItem(position);
        SimpleDraweeView user_photo = holder.getView(R.id.cp_img);
        TextView name = holder.getView(R.id.cp_name);
        TextView desc = holder.getView(R.id.cp_desc);
        ImageOptions.setImage(user_photo, scale.picPath);
        name.setText(scale.paperTitle);
        desc.setText("完成时间："+ Utils.dateFormatStr(scale.finishTime));
        return convertView;
    }
}
