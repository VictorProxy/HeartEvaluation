package com.vgtech.common.api;

import com.vgtech.common.URLAddr;

import java.io.Serializable;
import java.util.List;

/**
 * @author xuanqiang
 */
public class UserAccount extends AbsApiData implements Serializable {
    private static final long serialVersionUID = -3363651327267153786L;
    public String user_name;
    public String photo;
    public String xmpp_host;
    public int xmpp_port;
    public String user_id;


    public String getUrl(String uri) {
        return URLAddr.HOST + uri;
    }

    public String nickname() {
        return user_name;
    }

    public String getUid() {
        return String.valueOf(user_id);
    }
}
