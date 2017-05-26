package com.vgtech.vancloud.api;

import com.vgtech.common.api.AbsApiData;

/**
 * Created by vic on 2017/3/8.
 */
public class Counselor extends AbsApiData {
    public String counselorID;
    public String name;
    public String photoPath;
    public String degree;
    public int star;
    public String level;
    public String introduction;

    public String isCertification;
    public String isRealName;
    public int consultationTimes;
    public int praiseTimes;
    public int isMyPraise;
    public int isAttention;

    public String onlinePrice;
    public String phonePrice;
    public String facePrice;
    public String mbrID;
    public float getDegree() {
        try {
            return star;
        } catch (Exception e) {
            return 0;
        }
    }

    static String[] numArray = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    public String getLevel() {
        try {
            return "国家" + numArray[Integer.parseInt(level)] + "级咨询师";
        } catch (Exception e) {
            return level;
        }
    }
}
