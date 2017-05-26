package com.vgtech.vancloud.api;

import com.vgtech.common.api.AbsApiData;

/**
 * Created by vic on 2017/3/8.
 */
public class Product extends AbsApiData {
    public String productID;
    public String categoryID;
    public String productName;
    public String intro;
    public String picPath;
    public String fileType;//1音频 2图片 3文字 4视频
    public String filePath;
}
