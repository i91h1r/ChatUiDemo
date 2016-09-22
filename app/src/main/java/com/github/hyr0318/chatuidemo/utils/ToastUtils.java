package com.github.hyr0318.chatuidemo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类,优化Toast
 *
 */
public class ToastUtils {
    private static Toast mToast;

    /**
     * 显示对话框
     *
     * @param content 要显示的内容
     */
    public static void showToast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }
}
