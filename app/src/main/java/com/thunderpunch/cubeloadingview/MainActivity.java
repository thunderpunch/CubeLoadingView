package com.thunderpunch.cubeloadingview;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CubeDialog dlg = new CubeDialog(MainActivity.this, R.style.LoadingDialog);
        dlg.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dlg.dismiss();
            }
        }, 10000);

        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                findViewById(R.id.ll_main).setVisibility(View.VISIBLE);
            }
        });
    }
}
