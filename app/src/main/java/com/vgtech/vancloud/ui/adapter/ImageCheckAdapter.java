package com.vgtech.vancloud.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vgtech.common.api.ImageInfo;
import com.vgtech.common.view.photodraweeview.Attacher;
import com.vgtech.common.view.photodraweeview.PhotoDraweeView;
import com.vgtech.vancloud.R;

import java.io.File;
import java.util.List;

/**
 * Created by Duke on 2015/8/24.
 */
public class ImageCheckAdapter extends PagerAdapter implements View.OnClickListener {

    Context context;
    List<ImageInfo> list;
    private boolean isUser;

    public ImageCheckAdapter(Context context, List<ImageInfo> list, boolean isUser) {

        this.context = context;
        this.list = list;
        this.isUser = isUser;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.imagecheck_pager_layout, null);
//        final PhotoView photoView = new PhotoView(container.getContext());

        final PhotoDraweeView photoView = (PhotoDraweeView) view.findViewById(R.id.photo_view);
        final PhotoDraweeView userphoto_view = (PhotoDraweeView) view.findViewById(R.id.userphoto_view);
        LinearLayout delInfoLayout = (LinearLayout) view.findViewById(R.id.del_info);
        final ImageInfo imageInfo = list.get(position);
        boolean ifShowDelInfo = false;
        if (isUser) {
            delInfoLayout.setVisibility(View.GONE);
            if (TextUtils.isEmpty(imageInfo.url)) {
                userphoto_view.setImageResource(R.mipmap.default_user_photo_big);
            } else {
                userphoto_view.setPhotoUri(imageInfo.thumb, Uri.parse(imageInfo.url));
            }

            userphoto_view.setOnViewTapListener(new Attacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float v, float v1) {
                    ((Activity) context).onBackPressed();
                }
            });
            userphoto_view.setVisibility(View.VISIBLE);
            photoView.setVisibility(View.GONE);
            container.addView(view);
        } else {
            if (imageInfo.isLocal() && !TextUtils.isEmpty(imageInfo.url)) {
                String path = imageInfo.url.substring(7, imageInfo.url.length());
                File file = new File(path);
                if (!file.exists())
                    ifShowDelInfo = true;
                if (ifShowDelInfo) {
                    photoView.setVisibility(View.GONE);
                    delInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    photoView.setVisibility(View.VISIBLE);
                    delInfoLayout.setVisibility(View.GONE);
                    Uri uri = Uri.fromFile(file);
                    photoView.setPhotoUri(uri);
                }
                photoView.setOnViewTapListener(new Attacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float v, float v1) {
                        ((Activity) context).onBackPressed();
                    }
                });
            } else {
                photoView.setVisibility(View.VISIBLE);
                delInfoLayout.setVisibility(View.GONE);
                photoView.setPhotoUri(imageInfo.thumb, Uri.parse(imageInfo.url));
                photoView.setOnViewTapListener(new Attacher.OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float v, float v1) {
                        ((Activity) context).onBackPressed();
                    }
                });
            }
            container.addView(view);
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public void onClick(View v) {

//        switch (v.getId()) {
//            case R.id.save_img:
//                if (dialog != null && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//                String imgurl = v.getTag().toString();
//                downLoadImg(imgurl);
//                Log.e("ceshi", imgurl);
//                break;
//            default:
//                break;
//        }
//
    }
}
