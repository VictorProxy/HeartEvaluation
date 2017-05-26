package com.vgtech.vancloud.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.common.config.ImageOptions;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.api.Option;
import com.vgtech.vancloud.api.Product;
import com.vgtech.vancloud.utils.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vic on 2017/3/8.
 */
public class OptionAdapter extends BaseSimpleAdapter<Option> {
    public static final int OPTIONTYPE_SINGLE = 0;
    public static final int OPTIONTYPE_MUL = 1;
    public int optionType = OPTIONTYPE_SINGLE;
    public List<Option> selectedOption;

    public OptionAdapter(Context context) {
        super(context);
        selectedOption = new ArrayList<>();
    }

    public void setOptionType(int optionType) {
        this.optionType = optionType;
    }

    public void addSelectedOption(Option option) {
        if (optionType != OPTIONTYPE_MUL) {
            selectedOption.clear();
            selectedOption.add(option);
            notifyDataSetChanged();
        } else {
            if (selectedOption.contains(option)) {
                selectedOption.remove(option);
            } else {
                selectedOption.add(option);
            }
            notifyDataSetChanged();
        }
    }

    public List<Option> getSelectedOption() {
        return selectedOption;
    }

    @Override
    public int getItemResource(int viewType) {
        return R.layout.option_item;
    }

    @Override
    public View getItemView(int position, View convertView, ViewHolder holder) {
        Option option = getItem(position);
        TextView tv_option = holder.getView(R.id.tv_option);
        SimpleDraweeView iv_option = holder.getView(R.id.iv_option);
        ImageView iv_selected = holder.getView(R.id.iv_selected);

        String content = option.content;
        if (HtmlUtils.isImg(content)) {
            tv_option.setText(option.indexShow + "、");
            ImageOptions.setImage(iv_option, HtmlUtils.getImgSrc(mContext, content));
            iv_option.setVisibility(View.VISIBLE);
        } else {
            tv_option.setText(option.indexShow + "、" + option.content);
            iv_option.setVisibility(View.GONE);
        }
        if (selectedOption.contains(option)) {
            iv_selected.setVisibility(View.VISIBLE);
        } else {
            iv_selected.setVisibility(View.GONE);
        }
        return convertView;
    }
}
