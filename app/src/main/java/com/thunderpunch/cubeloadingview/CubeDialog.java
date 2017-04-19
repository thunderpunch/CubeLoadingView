package com.thunderpunch.cubeloadingview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by thunderpunch on 2017/4/1
 * Description:
 */


public class CubeDialog extends Dialog {

    public CubeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        View view = View.inflate(context, R.layout.layout_loading_dialog, null);
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
    }
}
