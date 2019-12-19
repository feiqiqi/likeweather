package com.feiqiqi.likeweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long clickTime = 0; // 第一次点击的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public void onBackPressed() {
        exit();
    }

    //双击退出程序
    private void exit() {

        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
        }

    }
}
