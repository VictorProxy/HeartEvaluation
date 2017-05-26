package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Reservation;
import com.vgtech.vancloud.api.ReservationMbr;

/**
 * Created by vic on 2017/3/8.
 */
public class ReservationMbrAdapter extends BaseSimpleAdapter<ReservationMbr> {
    public ReservationMbrAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.reservationmbr_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        ReservationMbr product = getItem(position);
        SimpleDraweeView user_photo = holder.getView(R.id.usericon);
        TextView name = holder.getView(R.id.tv_name);
        TextView desc = holder.getView(R.id.tv_title);
        TextView tv_timestamp = holder.getView(R.id.tv_timestamp);
        TextView tv_desc = holder.getView(R.id.tv_desc);
        ImageOptions.setImage(user_photo, product.mbrHeadImg);
        name.setText(product.mbrName);
        desc.setText(product.mbrMobile);
        String sex = "1".equals(product.mbrGender)?"男":"女";
        tv_timestamp.setText(sex+"  "+product.mbrAge+"岁");
        tv_desc.setText(product.reserDateShow);
        return convertView;
    }
}
