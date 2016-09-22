package com.github.hyr0318.chatuidemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.github.hyr0318.chatuidemo.utils.ToastUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.exceptions.HyphenateException;

public class RegisterActivity extends EaseBaseActivity {

    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.cv_add)
    CardView cvAdd;

    @InjectView(R.id.et_username)
    EditText et_username;

    @InjectView(R.id.et_password)
    EditText et_password;

    @InjectView(R.id.et_repeatpassword)
    EditText et_repeatpassword;
    private static final int REG_SUCCESS = 1;
    private static final int REG_FAILED = 2;
    private Handler mHandeler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case REG_SUCCESS:
                    ToastUtils.showToast(getApplicationContext(), "註冊成功");

                    finish();
                    break;

                case REG_FAILED:

                    String obj =(String) msg.obj;

                    ToastUtils.showToast(getApplicationContext(), obj);
                    break;

            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
    }


    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
            .inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }


            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }


            @Override
            public void onTransitionCancel(Transition transition) {

            }


            @Override
            public void onTransitionPause(Transition transition) {

            }


            @Override
            public void onTransitionResume(Transition transition) {

            }

        });
    }


    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0,
            fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }


            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }


    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0,
            cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }


            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }


    @Override
    public void onBackPressed() {
        animateRevealClose();
    }


    @OnClick(R.id.bt_go)
    public void onClick(View view) {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String rePwd = et_repeatpassword.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            ToastUtils.showToast(this, "用户名不能为空");
        }
        else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePwd)) {
            ToastUtils.showToast(this, "密码不能为空");
        }
        else if (!password.equals(rePwd)) {
            ToastUtils.showToast(this, "两次密码输入不一致,请重新输入");
            et_password.setText("");
            et_password.setFocusable(true);
        }else {
            regist(username, password);
        }

    }


    private void regist(final String username, final String password) {

        new Thread() {
            @Override public void run() {
                try {
                    EMClient.getInstance().createAccount(username, password);

                    mHandeler.sendEmptyMessage(REG_SUCCESS);

                    finish();

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    Log.d("REG_FAILED",e.getMessage());

                    Message message = new Message();

                    message.what = REG_FAILED;

                    message.obj = e.getMessage();

                    mHandeler.sendMessage(message);
                }
            }
        }.start();
    }
}
