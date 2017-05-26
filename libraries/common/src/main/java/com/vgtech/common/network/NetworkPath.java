package com.vgtech.common.network;

import android.content.Context;
import android.text.TextUtils;

import com.vgtech.common.PrfUtils;
import com.vgtech.common.network.android.FilePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangshaofang on 2015/8/5.
 */
public class NetworkPath {
    public static final int TYPE_JSONARRAY = 1;
    private String url;
    private Map<String, String> postValues;
    private Object extraData;
    private boolean cache;
    private int type;
    private boolean isVantop;

    public NetworkPath(String url) {
        this.url = url;
    }

    public NetworkPath(String url, Map<String, String> postValues) {
        this.url = url;
        this.postValues = postValues;
    }

    public NetworkPath(String url, Map<String, String> postValues, Context context) {
        this.url = url;
        this.postValues = postValues;
        if (postValues != null && !postValues.containsKey("mbrID")){
            postValues.put("mbrID", PrfUtils.getMbrId(context));
        }
    }


    public NetworkPath(String url, Map<String, String> postValues, FilePair obj) {
        this.url = url;
        this.postValues = postValues;
        this.extraData = obj;
    }

    public NetworkPath(String url, Map<String, String> postValues, List<FilePair> obj) {
        this.url = url;
        this.postValues = postValues;
        this.extraData = obj;
    }

    public boolean isCache() {
        return cache;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getPath() {
        return "NetworkPath{" +
                "url='" + url + '\'' +
                ", postValues=" + postValues +
                '}';
    }

    public boolean isVantop() {
        return isVantop;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getPostValues() {
        return postValues;
    }

    public void setPostValues(Map<String, String> postValues) {
        this.postValues = postValues;
    }

    public Object getExtraData() {
        return extraData;
    }

    public void setExtraData(Object extraData) {
        this.extraData = extraData;
    }
}
