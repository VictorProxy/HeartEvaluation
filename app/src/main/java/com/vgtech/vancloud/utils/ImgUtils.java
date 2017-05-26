package com.vgtech.vancloud.utils;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.vgtech.vancloud.R;

/**
 * Created by vic on 2017/3/8.
 */
public class ImgUtils {
    public static void setUserImg(SimpleDraweeView photoView,String photoUrl)
    {
        GenericDraweeHierarchy hierarchy = photoView.getHierarchy();
        hierarchy.setPlaceholderImage(R.mipmap.user_photo_default_small);
        hierarchy.setFailureImage(R.mipmap.user_photo_default_small);
        photoView.setImageURI(photoUrl);
    }
}
