package com.vgtech.vancloud.api;

import com.vgtech.common.api.AbsApiData;

/**
 * Created by vic on 2017/5/11.
 */
public class Timer extends AbsApiData {
    public WorkCalendar fisrtTime;
    public WorkCalendar secondTime;

    public String getFirstTime() {
        return fisrtTime.beginTime + "--" + fisrtTime.endTime;
    }

    public String getSecondTime() {
        if (secondTime != null) {
            return secondTime.beginTime + "--" + secondTime.endTime;
        }
        return "";
    }

    public WorkCalendar getFTime() {
        return fisrtTime;
    }
    public WorkCalendar getSTime() {
        return secondTime;
    }

    public void setFisrtTime(WorkCalendar fisrtTime) {
        this.fisrtTime = fisrtTime;
    }

    public void setSecondTime(WorkCalendar secondTime) {
        this.secondTime = secondTime;
    }
}
