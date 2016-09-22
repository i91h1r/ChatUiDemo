package com.github.hyr0318.chatuidemo;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.github.hyr0318.chatuidemo.application.ChatHelper;
import com.github.hyr0318.chatuidemo.runtimepermissions.PermissionsManager;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.orhanobut.logger.Logger;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarTab;
import com.roughike.bottombar.OnTabSelectListener;
import java.util.List;

public class LoginSuccessActivity extends EaseBaseActivity implements
    OnTabSelectListener {
    private HomeFragment homeFragment;
    private ConversationListFragment conversationListFragment;

    private TextView unreadLabel;
    private TextView unreadAddressLable;

    private Fragment[] fragments;

    private int index;
    private int currentTabIndex;
    private BottomBar bottomBar;


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        initview();

        bottomBar.setOnTabSelectListener(this);

    }


    private void initview() {

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        BottomBarTab tab1 = bottomBar.getTabWithId(R.id.tab_home);
        BottomBarTab tab2 = bottomBar.getTabWithId(R.id.tab_chat);

    }


    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            public void run() {
                updateUnreadLabel();

                if (conversationListFragment != null) {
                    conversationListFragment.refresh();

                }
            }
        });
    }


    EMMessageListener messageListener = new EMMessageListener() {

        @Override public void onMessageReceived(List<EMMessage> list) {

            ChatHelper.getInstance().getNotifier().onNewMesg(list);

            refreshUIWithMessage();

            EMClient.getInstance().chatManager().importMessages(list);//保存到数据库

        }


        @Override public void onCmdMessageReceived(List<EMMessage> list) {

        }


        @Override public void onMessageReadAckReceived(List<EMMessage> list) {

        }


        @Override public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }


        @Override public void onMessageChanged(EMMessage emMessage, Object o) {

        }
    };


    public void updateUnreadLabel() {
        int unreadMsgCountTotal = getUnreadMsgCountTotal();

        Logger.d(unreadMsgCountTotal);

        BottomBarTab tabWithId = bottomBar.getTabWithId(R.id.tab_chat);

        if (unreadMsgCountTotal > 0) {
            tabWithId.setBadgeCount(unreadMsgCountTotal);

        } else {
            tabWithId.removeBadge();
        }

    }


    private int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMsgsCount();
    }


    @Override protected void onStop() {

        EMClient.getInstance().chatManager().removeMessageListener(messageListener);

        ChatHelper.getInstance().popActivity(this);
        super.onStop();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override protected void onResume() {
        super.onResume();
        ChatHelper.getInstance().pushActivity(this);

        updateUnreadLabel();

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }


    @Override public void back(View view) {
        super.back(view);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    @Override public void onTabSelected(@IdRes int tabId) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (null != homeFragment) {
            ft.hide(homeFragment);
        }
        if (null != conversationListFragment) {
            ft.hide(conversationListFragment);
        }
        switch (tabId) {

            case R.id.tab_home:

                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    ft.add(R.id.contentContainer, homeFragment);
                }

                ft.show(homeFragment);
                break;

            case R.id.tab_chat:

                if (conversationListFragment == null) {
                    conversationListFragment = new ConversationListFragment();
                    ft.add(R.id.contentContainer, conversationListFragment);
                }

                ft.show(conversationListFragment);
                break;
        }

        ft.commit();
    }
}
