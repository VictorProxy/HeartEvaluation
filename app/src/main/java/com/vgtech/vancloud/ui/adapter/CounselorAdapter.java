package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Counselor;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.utils.ImgUtils;

/**
 * Created by vic on 2017/3/8.
 */
public class CounselorAdapter extends BaseSimpleAdapter<Counselor> {
    public CounselorAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.counselor_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Counselor counselor = getItem(position);
        SimpleDraweeView usericon = holder.getView(R.id.usericon);
        ImgUtils.setUserImg(usericon, counselor.photoPath);
        TextView nameTv = holder.getView(R.id.tv_name);
        nameTv.setText("[" + counselor.name + "]");
        TextView tv_title = holder.getView(R.id.tv_title);
        tv_title.setText(counselor.getLevel());
        TextView tv_desc = holder.getView(R.id.tv_desc);
        tv_desc.setText(counselor.introduction);
        return convertView;
    }
}
