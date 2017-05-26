package com.vgtech.vancloud.api;

import com.vgtech.common.api.AbsApiData;

/**
 * Created by vic on 2017/3/11.
 */
public class Option extends AbsApiData {
    public String optionID;
    public String content;
    public int score;
    public int isSelected;
    public String input;
    public String memo;
    public int gotoQuestionGroupNum;
    public int gotoQuestionNum;
    public String optionGroupID;
    public String showOrder;
    public String indexShow;
}
