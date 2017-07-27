package com.zyl.customkeyboard;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Description: 封装前，简单用法
 * Created by zouyulong on 2017/7/26.
 * Email : zouyulong@syswin.com
 * Person in charge :  zouyulong
 */
public class SimpleUsageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpleusage);
        final KeyboardView keyboardView = (KeyboardView) findViewById(R.id.keyboard_idcard);
        Keyboard keyboard = new Keyboard(this, R.xml.idcard_keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {

            }

            @Override
            public void onRelease(int primaryCode) {

            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {

            }

            @Override
            public void onText(CharSequence text) {

            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });
        findViewById(R.id.et_identity_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardView.setVisibility(View.VISIBLE);
            }
        });
    }
}
