package com.github.hyr0318.chatuidemo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.github.hyr0318.chatuidemo.application.ChatHelper;
import com.github.hyr0318.chatuidemo.utils.ToastUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseBaseActivity;

public class MainActivity extends EaseBaseActivity {

    @InjectView(R.id.et_username)
    EditText etUsername;
    @InjectView(R.id.et_password)
    EditText etPassword;
    @InjectView(R.id.bt_go)
    Button btGo;
    @InjectView(R.id.cv)
    CardView cv;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    private static final int SUCCESS = 1;
    private static final int FAILED = 2;
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    startActivity(intent, activityOptionsCompat.toBundle());
                    ToastUtils.showToast(getApplicationContext(), "登陸成功");
                    finish();
                    break;

                case FAILED:

                    String obj = (String) msg.obj;

                    ToastUtils.showToast(getApplicationContext(), msg.arg1 + obj);
                    break;
            }
        }
    };
    private Intent intent;
    private ActivityOptionsCompat activityOptionsCompat;
    private boolean autoLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ChatHelper.getInstance().isLoggedIn()) {

            autoLogin = true;

            startActivity(new Intent(getApplicationContext(), LoginSuccessActivity.class));

            finish();

            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }


    @OnClick({ R.id.bt_go, R.id.fab })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(this, fab,
                            fab.getTransitionName());
                    startActivity(new Intent(this, RegisterActivity.class), options.toBundle());
                } else {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.bt_go:
                activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                intent = new Intent(this, LoginSuccessActivity.class);

                String username = etUsername.getText().toString().trim();
                String psd = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    ToastUtils.showToast(this, "用戶名爲空");
                } else if (TextUtils.isEmpty(psd)) {
                    ToastUtils.showToast(this, "密碼爲空");
                } else {
                    EMClient.getInstance().login(username, psd, new EMCallBack() {
                        @Override public void onSuccess() {
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();

                            Message message = new Message();

                            message.what = SUCCESS;

                            mHandler.sendMessage(message);

                        }


                        @Override public void onError(int i, String s) {
                            Message message = new Message();

                            message.what = FAILED;

                            message.arg1 = i;

                            message.obj = s;

                            mHandler.sendMessage(message);

                        }


                        @Override public void onProgress(int i, String s) {

                        }
                    });
                }

                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
