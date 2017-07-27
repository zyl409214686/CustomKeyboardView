package com.zyl.customkeyboardview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Description: 自定义KeyboardEditText
 * Created by zouyulong on 2017/7/26.
 * Email : zouyulong@syswin.com
 * Person in charge :  zouyulong
 */

public class CustomKeyboardEditText extends EditText implements KeyboardView.OnKeyboardActionListener,
        View.OnClickListener {
    private Keyboard mKeyboard;
    private KeyboardView mKeyboardView;
    private PopupWindow mKeyboardWindow;
    private View mDecorView;

    public CustomKeyboardEditText(Context context) {
        this(context, null);
    }

    public CustomKeyboardEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomKeyboardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initKeyboardView(context, attrs);
    }

    private void initKeyboardView(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Keyboard);
        if (!array.hasValue(R.styleable.Keyboard_xml)) {
            throw new IllegalArgumentException("you need add keyboard_xml argument!");
        }
        int xmlId = array.getResourceId(R.styleable.Keyboard_xml, 0);
        mKeyboard = new Keyboard(context, xmlId);
        mKeyboardView = (KeyboardView) LayoutInflater.from(context).inflate(R.layout.keyboard_view, null);
        //键盘关联keyboard对象
        mKeyboardView.setKeyboard(mKeyboard);
        //关闭键盘按键预览效果，如果按键过小可能会比较适用。
        mKeyboardView.setPreviewEnabled(false);
        //设置键盘事件
        mKeyboardView.setOnKeyboardActionListener(this);
        //将keyboardview放入popupwindow方便显示以及位置调整。
        mKeyboardWindow = new PopupWindow(mKeyboardView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        array.recycle();
        //设置点击事件，点击后键盘弹起，系统键盘收起。
        setOnClickListener(this);
        //屏蔽当前edittext的系统键盘
        notSystemSoftInput();
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Editable editable = this.getText();
        //获取光标偏移量下标
        int startIndex = this.getSelectionStart();
        switch (primaryCode) {
            case Keyboard.KEYCODE_CANCEL:// 隐藏键盘
                hideKeyboard();
                break;
            case Keyboard.KEYCODE_DELETE:// 回退
                if (editable != null && editable.length() > 0) {
                    if (startIndex > 0) {
                        editable.delete(startIndex - 1, startIndex);
                    }
                }
                break;
            case 9994://左移
                setSelection(startIndex-1);
                break;
            case 9995://重输
                editable.clear();
                break;
            case 9996://右移
                if (startIndex < length()) {
                    setSelection(startIndex + 1);
                }
                break;
            default:
                editable.insert(startIndex, Character.toString((char) primaryCode));
                break;
        }
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

    /**
     * 根据key code 获取 Keyboard.Key 对象
     * @param primaryCode
     * @return
     */
    private Keyboard.Key getKeyByKeyCode(int primaryCode) {
        if(null != mKeyboard){
            List<Keyboard.Key> keyList = mKeyboard.getKeys();
            for (int i =0,size= keyList.size(); i < size; i++) {
                Keyboard.Key key = keyList.get(i);

                int codes[] = key.codes;

                if(codes[0] == primaryCode){
                    return key;
                }
            }
        }

        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (null != mKeyboardWindow) {
                if (mKeyboardWindow.isShowing()) {
                    mKeyboardWindow.dismiss();
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (!focused) {
            hideKeyboard();
        } else {
            hideSysInput();
            showKeyboard();
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDecorView = ((Activity) getContext()).getWindow().getDecorView();
        hideSysInput();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hideKeyboard();
        mKeyboardWindow = null;
        mKeyboardView = null;
        mKeyboard = null;
        mDecorView = null;
    }

    /**
     * 显示自定义键盘
     */
    private void showKeyboard() {
        if (null != mKeyboardWindow) {
            if (!mKeyboardWindow.isShowing()) {
                mKeyboardView.setKeyboard(mKeyboard);
                mKeyboardWindow.showAtLocation(this.mDecorView, Gravity.BOTTOM, 0, 0);
            }
        }
    }

    /**
     * 屏蔽系统输入法
     */
    private void notSystemSoftInput(){
        if (Build.VERSION.SDK_INT <= 10) {
            setInputType(InputType.TYPE_NULL);
        } else {
            ((Activity)getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(this, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 隐藏自定义键盘
     */
    private void hideKeyboard() {
        if (null != mKeyboardWindow) {
            if (mKeyboardWindow.isShowing()) {
                mKeyboardWindow.dismiss();
            }
        }
    }

    /**
     * 隐藏系统键盘
     */
    private void hideSysInput() {
        if (this.getWindowToken() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        requestFocus();
        requestFocusFromTouch();
        hideSysInput();
        showKeyboard();
    }
}
