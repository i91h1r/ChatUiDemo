package com.github.hyr0318.chatuidemo.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.github.hyr0318.chatuidemo.ChatActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.orhanobut.logger.Logger;
import java.util.List;

/**
 * Description:
 * 作者：hyr on 2016/9/19 11:34
 * 邮箱：2045446584@qq.com
 */
public class ChatHelper {

    private static ChatHelper instance = null;
    private Context mContext;
    private EaseUI easeUI;
    protected EMMessageListener messageListener = null;

    protected static final String TAG = "ChatHelper";


    public static ChatHelper getInstance() {

        if (instance == null) {
            instance = new ChatHelper();
        }
        return instance;
    }


    public void init(Context context) {

        EMOptions options = initChatOptions();

        if (EaseUI.getInstance().init(context, options)) {

            this.mContext = context;

            easeUI = EaseUI.getInstance();

        }

        //全局注册消息监听 后台通知栏显示消息
        registerMessageListener();

        setNotificationInfoProvider();



    }


    private void setNotificationInfoProvider() {
        /**
         * 设置通知栏显示，不设置使用默认的样式
         */
        easeUI.getNotifier()
            .setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

                @Override
                public String getTitle(EMMessage message) {

                    return null;
                }


                @Override
                public int getSmallIcon(EMMessage message) {

                    return 0;
                }


                @Override
                public String getDisplayedText(EMMessage message) {

                    String ticker = EaseCommonUtils.getMessageDigest(message, mContext);
                    if (message.getType() == EMMessage.Type.TXT) {
                        ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                    }

                    return message.getFrom() + ": " + ticker;

                }


                @Override
                public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {

                    if (fromUsersNum == 1) {
                        return message.getFrom() + " 发送了 " + messageNum + "条消息给你";
                    } else {
                        return fromUsersNum + "个联系人发送了" + messageNum + "条消息给你";
                    }

                }


                @Override
                public Intent getLaunchIntent(EMMessage message) {

                    //打开聊天界面，不设置默认打开会话界面
                    Intent intent = new Intent(mContext, ChatActivity.class);

                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // single chat message
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else {

                        intent.putExtra("userId", message.getTo());
                        if (chatType == EMMessage.ChatType.GroupChat) {
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }

                    }
                    return intent;
                }
            });
    }


    /**
     * 注册消息监听
     */
    private void registerMessageListener() {

        messageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {

                    Logger.d(message.getMsgId());

                    if (!easeUI.hasForegroundActivies()) {
                        /**
                         * 程序在后台时，通知栏显示消息
                         */
                        getNotifier().onNewMsg(message);
                    }
                }
            }


            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {
                    Logger.d("receive command message");

                    //get message body
                    EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action();//获取自定义action
                    //red packet code : 处理红包回执透传消息
                    if (!easeUI.hasForegroundActivies()) {
                       /* if (action.equals(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION)){
                            RedPacketUtil.receiveRedPacketAckMessage(message);
                            broadcastManager.sendBroadcast(new Intent(RedPacketConstant.REFRESH_GROUP_RED_PACKET_ACTION));
                        }*/
                    }
                    //end of red packet code
                    //获取扩展属性 此处省略
                    //maybe you need get extension of your message
                    //message.getStringAttribute("");
                    Logger.d(
                        String.format("Command：action:%s,message:%s", action, message.toString()));
                }
            }


            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
            }


            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            }


            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }


    private EMOptions initChatOptions() {
        EMOptions options = new EMOptions();

        options.setAcceptInvitationAlways(false);

        options.setRequireAck(true);

        return options;

    }


    public EaseNotifier getNotifier() {
        return easeUI.getNotifier();
    }


    /**
     * 判断是否登陆过
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }


    public void popActivity(Activity activity) {
        easeUI.popActivity(activity);
    }


    public void pushActivity(Activity activity) {
        easeUI.pushActivity(activity);
    }


    /**
     * 退出登录
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");

                if (callback != null) {
                    callback.onSuccess();
                }

            }


            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }


            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");

                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }


    void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
