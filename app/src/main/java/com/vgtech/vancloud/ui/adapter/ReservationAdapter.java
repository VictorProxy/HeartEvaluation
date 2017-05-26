package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.api.Reservation;

/**
 * Created by vic on 2017/3/8.
 */
public class ReservationAdapter extends BaseSimpleAdapter<Reservation> {
    public ReservationAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.reservation_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Reservation product = getItem(position);
        SimpleDraweeView user_photo = holder.getView(R.id.usericon);
        TextView name = holder.getView(R.id.tv_name);
        TextView desc = holder.getView(R.id.tv_title);
        TextView tv_timestamp = holder.getView(R.id.tv_timestamp);
        TextView tv_desc = holder.getView(R.id.tv_desc);
        ImageOptions.setImage(user_photo, product.counselorPhotoPath);
        name.setText("["+product.counselorName+"]");
        desc.setText(product.counselorLevel);
        tv_timestamp.setText(product.reserDateShow);
        tv_desc.setText(product.reserStatus);
        return convertView;
    }
}
