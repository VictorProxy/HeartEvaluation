package com.vgtech.vancloud.ui.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.vgtech.common.PrfUtils;
import com.vgtech.common.network.NetworkManager;
import com.vgtech.common.view.swipemenu.SwipeMenu;
import com.vgtech.common.view.swipemenu.SwipeMenuCreator;
import com.vgtech.common.view.swipemenu.SwipeMenuItem;
import com.vgtech.common.view.swipemenu.SwipeMenuListView;
import com.vgtech.vancloud.R;
import com.vgtech.vancloud.ui.adapter.DataAdapter;
import com.vgtech.vancloud.ui.chat.controllers.AvatarController;
import com.vgtech.vancloud.ui.chat.controllers.Controller;
import com.vgtech.vancloud.ui.chat.controllers.XmppController;
import com.vgtech.vancloud.ui.chat.models.ChatGroup;
import com.vgtech.vancloud.ui.view.groupimageview.NineGridImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import static com.vgtech.vancloud.ui.chat.models.ChatGroup.GroupTypeChat;

/**
 * @author xuanqiang
 */
public class MessagesFragment extends RoboFragment {
    @InjectView(R.id.messages_empty)
    View emptyView;
    @InjectView(R.id.messages_list)
    SwipeMenuListView listView;

    private View headView;

//    private boolean haveSysNotice = true;//TODO 是否存在系统通知

    private NetworkManager mNetworkManager;
    private final int CALLBACK_TODOLIST = 1;


    public static final String RECEIVER_XMPPMESSAGE = "RECEIVER_XMPPMESSAGE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_list, null);
        TextView titleTv = (TextView) view.findViewById(android.R.id.title);
        titleTv.setText("消息");
        View backView = view.findViewById(R.id.btn_back);
        backView.setVisibility(View.VISIBLE);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new Adapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (controller.isFastDoubleClick()) {
                    return;
                }
                controller.pushFragment(UsersMessagesFragment.newInstance(adapter.dataSource.get(position - listView.getHeaderViewsCount()), null));
            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu, int position) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xff, 0x00,
                        0x00)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        // step 2. listener item click event
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu,
                                           int index) {
                switch (index) {
                    case 0: {
                        final ChatGroup group = adapter.dataSource.get(position);
                        group.deletefromMessage();
                        reloadData();
                    }
                    break;
                }
                return false;
            }
        });
        // set creator
        listView.setMenuCreator(creator);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                ChatGroup group = adapter.dataSource.get(position);
                showDeleatDialog(group);
                return true;
            }
        });
        listView.setAdapter(adapter);
        reloadData();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVER_XMPPMESSAGE);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 显示删除聊天提示Dialog
     *
     * @param group
     */
    public void showDeleatDialog(final ChatGroup group) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.DialogItemLargeFont);
        new AlertDialog.Builder(contextThemeWrapper).setTitle(group.getDisplayNick()).setItems(new String[]{getString(R.string.delete_chat)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dialog.dismiss();
                    group.deletefromMessage();
                    reloadData();
                }
            }
        }).show();
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void reloadData() {
        new AsyncTask<Void, Void, List<ChatGroup>>() {
            @Override
            protected List<ChatGroup> doInBackground(Void... params) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return null;
                }
                List<ChatGroup> chatGroupList = ChatGroup.findAllbyChat(PrfUtils.getMbrId(getActivity()));
                return chatGroupList;
            }

            @Override
            protected void onPostExecute(List<ChatGroup> chatGroupList) {
                if (chatGroupList != null) {
                    adapter.dataSource.clear();
                    adapter.dataSource.addAll(chatGroupList);
                    adapter.notifyDataSetChanged();
                    emptyView.setVisibility(adapter.dataSource.size() == 0 ? View.VISIBLE : View.GONE);
                    controller.updateMessagesBarNum(adapter.dataSource);
                }
            }
        }.execute();
    }

    @Override
    public void onDetach() {
        eventManager.unregisterObserver(this, OnEvent.class);
        super.onDetach();
    }

    private class Adapter extends DataAdapter<ChatGroup> {
        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = getLayoutInflater(null).inflate(R.layout.messages_item, null);
//                assert convertView != null;
                viewHolder.avatarView = (SimpleDraweeView) convertView.findViewById(R.id.avatar);
                viewHolder.avatarContainer = (NineGridImageView) convertView.findViewById(R.id.avatar_container);
                viewHolder.numButton = (TextView) convertView.findViewById(R.id.messages_item_num);
                viewHolder.nameLabel = (TextView) convertView.findViewById(R.id.messages_item_name);
                viewHolder.timeLabel = (TextView) convertView.findViewById(R.id.messages_item_time);
                viewHolder.contentLabel = (TextView) convertView.findViewById(R.id.messages_item_content);
                viewHolder.failView = convertView.findViewById(R.id.messages_item_fail);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ChatGroup group = dataSource.get(position);
            setView(viewHolder, group);
            return convertView;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        class ViewHolder {
            SimpleDraweeView avatarView;
            NineGridImageView avatarContainer;
            TextView numButton;
            TextView nameLabel;
            TextView timeLabel;
            TextView contentLabel;
            View failView;
        }


        void setView(ViewHolder viewHolder, ChatGroup group) {
            viewHolder.failView.setVisibility(group.isFailure() ? View.VISIBLE : View.GONE);

            String name = group.peopleNum > 0 ? group.getDisplayNick() + "(" + group.peopleNum + getString(R.string.people) + ")" : group.getDisplayNick();
            viewHolder.nameLabel.setText(name);
            viewHolder.timeLabel.setText(group.getDisplayTime());
            viewHolder.contentLabel.setText(EmojiFragment.getEmojiContentWithAt(getActivity(), group.getContent(getResources())));
            List<String> avatars = null;
            if (GroupTypeChat.equals(group.type)) {
                avatars = new ArrayList<String>(1);
                avatars.add(group.avatar);
            } else {
                try {
                    avatars = new Gson().fromJson(group.avatar, new TypeToken<List<String>>() {
                    }.getType());
                } catch (JsonSyntaxException ignored) {
                    if (!TextUtils.isEmpty(group.avatar)) {
                        String[] avatarArray = group.avatar.split(",");
                        avatars = new ArrayList(Arrays.asList(avatarArray));
                    }
                }
                if (avatars == null) {
                    avatars = new ArrayList<String>(1);
                    avatars.add("");
                }
            }
            avatarController.setAvatarContainer(viewHolder.avatarView, viewHolder.avatarContainer, avatars);
            viewHolder.numButton.setText(String.valueOf(group.unreadNum < 100 ? group.unreadNum : "N"));
            viewHolder.numButton.setVisibility(group.unreadNum > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    void handleEvent(@Observes OnEvent event) {
        eventHandler.sendMessage(eventHandler.obtainMessage(0, event));
    }

    private Handler eventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (getActivity() != null)
                reloadData();
        }
    };

    private Adapter adapter;
    @Inject
    AvatarController avatarController;
    @Inject
    XmppController xmpp;
    @Inject
    EventManager eventManager;
    @Inject
    public Controller controller;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNetworkManager != null)
            mNetworkManager.cancle(this);
        if (mReceiver != null)
            getActivity().unregisterReceiver(mReceiver);
        mReceiver = null;
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (RECEIVER_XMPPMESSAGE.equals(action)) {
                reloadData();
            }
        }
    };
}
