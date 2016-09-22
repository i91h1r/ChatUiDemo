package com.github.hyr0318.chatuidemo.application;

import android.app.Application;
import android.content.Context;
import com.melink.bqmmsdk.sdk.BQMM;

/**
 * Description:
 * 作者：hyr on 2016/9/19 11:33
 * 邮箱：2045446584@qq.com
 */
public class ChatApplication extends Application {

    private Context mContext ;
    @Override public void onCreate() {
        super.onCreate();

        mContext = this ;

        ChatHelper.getInstance().init(mContext);

        BQMM.getInstance().initConfig(mContext,"9f6656f59f7741c896c283e72c1b03c0", "e7fae52270ab4c4badf5550ddb25360f");

    }
}
