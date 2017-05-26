package com.vgtech.vancloud.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.vgtech.common.api.UserAccount;
import com.vgtech.vancloud.ui.chat.models.ChatUser;

import java.io.Serializable;

/**
 * @author xuanqiang
 */
public class Staff implements Serializable {
    private static final long serialVersionUID = 8975124571869805013L;

    //    @SerializedName("staff_no")
//    public String id;
//    @SerializedName("staff_name")
//    public String nick;
//    @SerializedName("avatar_b64")
//    public String avatar;
    @SerializedName("pos")
    public String post;
    @SerializedName("e_mail")
    public String email;
    @SerializedName("hasXmpp")
    public boolean hasXmpp;


    @SerializedName("userid")
    public String id;
    @SerializedName("name")
    public String nick;
    @SerializedName("photo")
    public String avatar;
//    @SerializedName("pos")
//    public String post;
//    @SerializedName("e_mail")
//    public String email;
//    @SerializedName("hasXmpp")
//    public boolean hasXmpp;

    public boolean hasGroup;

    public String username;
    public String pin;
    public String sec;
    public boolean checked;
    public boolean selected;

    public Staff() {
    }

    public Staff(final String id, final String name, final String nick, final String avatar) {
        this.id = id;
        this.username = name;
        this.nick = nick;
        this.avatar = avatar;
    }

    public Staff(final Subject subject, String loginname) {
        this(subject.uid, loginname, subject.getNick(), subject.avatar);
    }

    public Staff(final UserAccount account) {
        this(account.getUid(), account.nickname(), account.nickname(), account.photo);
    }

    public Staff(final ChatUser user) {
        this(user.uid, user.name, user.nick, user.avatar);
    }

    public String name() {
//        if (username == null) {
//            username = XmppController.logname(id);
//        }
        return username;
    }

}

