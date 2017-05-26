package com.vgtech.vancloud.ui.chat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.vgtech.common.view.ActionSheetDialog;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.models.Staff;
import com.vgtech.vancloud.models.Subject;
import com.vgtech.vancloud.ui.chat.controllers.AvatarController;
import com.vgtech.vancloud.ui.chat.controllers.MediaController;
import com.vgtech.vancloud.ui.chat.controllers.XmppController;
import com.vgtech.vancloud.ui.chat.models.ChatGroup;
import com.vgtech.vancloud.ui.chat.models.ChatMessage;
import com.vgtech.vancloud.ui.chat.models.ChatMessage.ShowType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import roboguice.event.EventManager;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;
import roboguice.util.Strings;

import static com.vgtech.vancloud.ui.chat.OnEvent.EventType;
import static com.vgtech.vancloud.ui.chat.models.ChatMessage.MessageTypeFile;
import static com.vgtech.vancloud.ui.chat.models.ChatMessage.MessageTypeGps;
import static com.vgtech.vancloud.ui.chat.models.ChatMessage.MessageTypeNormal;


/**
 * @author xuanqiang
 */
public class AbstractUsersMessagesFragment extends ActionBarFragment implements ContactsListener {
    @InjectView(R.id.user_messages_listView)
    PullToRefreshListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return createContentView(R.layout.user_messages);
    }

    private int lastItem;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleView.getLayoutParams();
//        assert lp != null;
//        lp.addRule(RelativeLayout.RIGHT_OF, R.id.actionbar_left);
//        lp.addRule(RelativeLayout.LEFT_OF, R.id.actionbar_right);
        titleView.setText(getTitle());
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                refreshView.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
                Long maxId = getMinId();
                if (maxId != null) {
                    adapter.dataSource.addAll(0, group.getMessages(maxId));
                    adapter.notifyDataSetChanged();
                }
                refreshView.getRefreshableView().post(new Runnable() {
                    @Override
                    public void run() {
                        refreshView.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//                        refreshView.onRefreshComplete();
                        refreshView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (refreshView.isRefreshing()) {
                                    refreshView.setState(PullToRefreshBase.State.RESET);
                                }
                                if (adapter.dataSource.size() > 20)
                                    listView.getRefreshableView().setSelection(20);
                            }
                        }, 225);
                    }
                });
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("onScrollStateChanged", "onScrollStateChanged---" + scrollState);
//                adapter.notifyDataSetChanged();//提醒adapter更新
//                com.vgtech.personaledition.view.setSelection(lastItem - 1);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                lastItem = firstVisibleItem + visibleItemCount;

            }
        });
        listView.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    public boolean copyFile(String oldPath, String newPath) {
        boolean isok = true;
        try {
            int byteread;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            } else {
                isok = false;
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;

    }

    public static File getTempDir(Context context) {
        String basePath = Environment.getExternalStorageDirectory().getPath()
                + "/" + "VanCloud" + "";
        File file = new File(basePath);
        if (file == null || !file.exists()) {
            file.mkdirs();
        }
        return file;
        // return context.getDir(TEMP_DIR_NAME, Context.MODE_WORLD_READABLE
        // | Context.MODE_WORLD_WRITEABLE);
    }

    public String getTitle() {
        if (ChatGroup.GroupTypeGroup.equals(group.type)) {
            if (Strings.notEmpty(group.groupNick)) {
                return group.groupNick + "(" + group.peopleNum + getString(R.string.people) + ")";
            }
            return getString(R.string.group_chat) + "(" + group.peopleNum + ")";
        } else {
            String title;
            if (group.user() != null)
                title = group.user().nick;
            else
                title = group.nick;
            return title;
        }
    }

    private Long getMinId() {
        if (adapter.dataSource.size() > 0) {
            ChatMessage chatMessage = adapter.dataSource.get(0);
            return chatMessage.getId();
        }
        return null;
    }

    @Override
    public void selectedContacts(List<Staff> contactses, ChatGroup group) {
        xmpp.chat(contactses, group, forwardMessage);
    }

    //region fail listener
    View.OnClickListener failListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final ChatMessage message = (ChatMessage) view.getTag();
            new AlertDialog.Builder(getActivity()).setTitle(R.string.prompt)
                    .setMessage(R.string.resend)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            if (MessageTypeFile.equals(message.type)) {
                                xmpp.updateSendStatus(message, false);
                                xmpp.sendFile(group, message.getFile(), message);
                                return;
                            }

                            new RoboAsyncTask<Void>(getActivity()) {
                                @Override
                                protected void onSuccess(Void aVoid) throws Exception {
                                }

                                @Override
                                public Void call() throws Exception {
                                    xmpp.updateSendStatus(message, false);
                                    Subject subject = new Subject(controller.account(), message, group.nick);
                                    xmpp.send(xmpp.newMessage(group.name, group.getChatType(), message.content, subject), message);
                                    return null;
                                }
                            }.execute();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    };
    //endregion

    protected ChatGroup group;
    ChatMessage forwardMessage;
    UsersMessagesAdapter adapter;
    @Inject
    InputMethodManager imManager;
    @Inject
    LayoutInflater inflater;
    @Inject
    XmppController xmpp;
    @Inject
    AvatarController avatarController;
    @Inject
    EventManager eventManager;

}
