package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.api.Scale;

/**
 * Created by vic on 2017/3/8.
 */
public class ProductAdapter extends BaseSimpleAdapter<Product> {
    public ProductAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.article_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Product product = getItem(position);
        SimpleDraweeView user_photo = holder.getView(R.id.cp_img);
        TextView name = holder.getView(R.id.cp_name);
        TextView desc = holder.getView(R.id.cp_desc);
        ImageOptions.setImage(user_photo, product.picPath);
        name.setText(product.productName);
        desc.setText(product.intro);
        return convertView;
    }
}
