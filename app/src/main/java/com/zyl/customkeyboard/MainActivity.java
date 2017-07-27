package com.zyl.customkeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Description: 主activity
 * Created by zouyulong on 2017/7/26.
 * Email : zouyulong@syswin.com
 * Person in charge :  zouyulong
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_unwrapper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.setClass(MainActivity.this, SimpleUsageActivity.class);
                startActivity(in);
            }
        });
        findViewById(R.id.bt_wrappered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent();
                in.setClass(MainActivity.this, KeyboardEditTextActivity.class);
                startActivity(in);
            }
        });
    }
}
