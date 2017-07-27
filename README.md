# CustomKeyboardView
android自定义键盘、自定义身份证键盘、支持拓展。
android 系统键盘支持的点已经比较丰富了， 但是有时候某一些需求还不能满足我们的需求。最近公司应用到了实名认证相关的功能，这部分需要一个身份证的EditText, 自然也需要一个身份证的键盘，奈何系统没有这种键盘，只能自定义一个。

首先来看android SDK为我们提供**Keyboard**的这个类。

1、Keyboard xml描述文件

![来源android开发者官网](http://upload-images.jianshu.io/upload_images/2229793-a623286be02606ce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

上面已经描述的很清晰了， 它用来加载一个键盘和存储按键属性的一个描述XML。我们在res/xml 目录下新建一个idcard_keyboard.xml文件。

```
<?xml version="1.0" encoding="utf-8"?>
<Keyboard xmlns:android="http://schemas.android.com/apk/res/android"
    android:horizontalGap="2dp"
    android:verticalGap="2dp"
    android:keyHeight="60dp"
    android:keyWidth="33%p">
    <Row>
        <Key
            android:codes="49"
            android:keyLabel="1" />
        <Key
            android:codes="50"
            android:keyLabel="2" />
        <Key
            android:codes="51"
            android:keyLabel="3" />
    </Row>
    <Row>
        <Key
            android:codes="52"
            android:keyLabel="4" />
        <Key
            android:codes="53"
            android:keyLabel="5" />
        <Key
            android:codes="54"
            android:keyLabel="6" />
    </Row>
    <Row>
        <Key
            android:codes="55"
            android:keyLabel="7" />
        <Key
            android:codes="56"
            android:keyLabel="8" />
        <Key
            android:codes="57"
            android:keyLabel="9" />
    </Row>
    <Row>
        <Key
            android:codes="88"
            android:keyLabel="X"
            />
        <Key
            android:codes="48"
            android:keyLabel="0" />
        <Key
            android:codes="-5"
            android:keyIcon="@mipmap/keyboard_del"/>
    </Row>
</Keyboard>
```
这里主要介绍一些常用的类属性，更多请参考[andorid官网](https://developer.android.google.cn/reference/android/inputmethodservice/Keyboard.html)。
- Keyboard类：存储键盘以及按键相关信息。
android:horizontalGap 按键之间默认的水平间距。
android:verticalGap 按键之间默认的垂直间距。
android:keyHeight 按键的默认高度，以像素或显示高度的百分比表示。
android:keyWidth: 按键的默认宽度，以像素或显示宽度的百分比表示。
- Row：为包含按键的容器。
- Key:  用于描述键盘中单个键的位置和特性。
android:codes  该键输出的unicode值。
android:isRepeatable 这个属性如果设置为true，那么当长按该键时就会重复接受到该键上的动作，在 删除键键 和 空格键 上通常设为true。
android:keyLabel 显示在按键上的文字。
android:keyIcon 与keyLabel是二选一关系，它会代替文字以图标的形式显示在键上。

有了Keyboard来存储键盘相关信息了，那么键盘如何这些信息呢？这时候用到android SDK为我们提供的另外一个类***KeyboardView***。

2、KeyboardView 

![来自android开发者官网.png](http://upload-images.jianshu.io/upload_images/2229793-8bf9a16ad3887345.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

KeyboardView是一个渲染虚拟键盘的View。 它处理键的渲染和检测按键和触摸动作。显然我们需要KeyboardView来对Keyboard里的数据进行渲染并呈现给我们以及相关的点击事件做处理。
1）`//设置keyboard与KeyboardView相关联的方法。
    public void setKeyboard(Keyboard keyboard) `
2）`//设置虚拟键盘事件的监听，此方法必须设置，不然会报错。
    public void setOnKeyboardActionListener(OnKeyboardActionListener    listener)`
步骤上呢，做完第一步的关联，并设置第二步的事件，调用`KeyboardView.setVisible(true);`键盘就可以显示出来了， 是不是很简单。不过到这里还没有结束哦，接下来我们为了使用上的便利要进行相应的封装。
封装
这里我们通过继承EditText来对Keyboard与KeyboardView进行封装。
attr.xml文件，这里我们需要通过一个xml类型的自定义属性引入我们的键盘描述文件。
```
<!-- attr.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="Keyboard">
        <attr name="xml" format="reference"/>
    </declare-styleable>
</resources>
```
还需要一个keyboardView 布局文件
```
<!-- KeyboardView 布局文件 -->
<?xml version="1.0" encoding="utf-8"?>
<android.inputmethodservice.KeyboardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:focusable="true"
    android:focusableInTouchMode="true"
    android:keyTextColor="#000000"
    android:keyBackground="@drawable/bg_keyboard_btn"
    android:keyTextSize="31dp"
    android:labelTextSize="23.04sp"
    android:background="#e6e6e6"
    />
```
万事俱备、主角登场~~~
```
/**
 * Description: 自定义键盘类KeyboardEditText
 * Created by zouyulong on 2017/7/26.
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
```
代码中注释写的已经很清晰了，就不做一一的讲解了。

结尾
- 该自定义组件目前只是支持了身份证键盘，xml下只提供了身份证键盘的描述文件。如果需要其他键盘可以自己定义xml文件，如果有特殊点击事件，逻辑放入`public void onKey(int primaryCode, int[] keyCodes) ` case 相应的keycode逻辑下即可。

- 代码已放入[github https://github.com/zyl409214686/CustomKeyboardView](https://github.com/zyl409214686/CustomKeyboardView)，欢迎提 issues、star、Fork。

- 效果图：

![screenshot.png](http://upload-images.jianshu.io/upload_images/2229793-d0b19cd9efa3ff64.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

参考：
- [android开发者官网](https://developer.android.google.cn/reference/android/inputmethodservice/Keyboard.html)
- [Android 总结：自定义键盘实现原理和三种实例详解](http://blog.csdn.net/u014136472/article/details/50257245)