package com.github.uiautomator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.io.UnsupportedEncodingException;

public class FastInputIME extends InputMethodService {
    private static final String TAG = "FastInputIME";
    private static final String USB_STATE_CHANGE = "android.hardware.usb.action.USB_STATE";

    private BroadcastReceiver mReceiver = null;

    @Override
    public View onCreateInputView() {
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        IntentFilter filter = new IntentFilter();
        filter.addAction(USB_STATE_CHANGE);
        filter.addAction("ADB_INPUT_TEXT");
        filter.addAction("ADB_INPUT_CHARS");
        filter.addAction("ADB_EDITOR_CODE");
        mReceiver = new InputMessageReceiver();
        registerReceiver(mReceiver, filter);

        Keyboard keyboard = new Keyboard(this, R.xml.number_pad);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(new MyKeyboardActionListener());
        return keyboardView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    class InputMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case USB_STATE_CHANGE:
                    if (!intent.getExtras().getBoolean("connected")) {
                        final IBinder token = getWindow().getWindow().getAttributes().token;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.switchToLastInputMethod(token);
                    }
                    break;
                case "ADB_INPUT_TEXT":
                    String msg = intent.getStringExtra("text");
                    if (msg == null) {
                        return;
                    }
                    Log.i(TAG, "input text(base64): " + msg);
                    byte[] data = Base64.decode(msg, Base64.DEFAULT);
                    try {
                        String text = new String(data, "UTF-8");
                        InputConnection ic = getCurrentInputConnection();
                        ic.commitText(text, 1);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case "ADB_CLEAR_TEXT":
                    // TODO: not finished yet
                    break;
            }
        }
    }

    private class MyKeyboardActionListener implements KeyboardView.OnKeyboardActionListener {

        @Override
        public void onPress(int i) {

        }

        @Override
        public void onRelease(int i) {

        }

        @Override
        public void onKey(int i, int[] ints) {

        }

        @Override
        public void onText(CharSequence charSequence) {

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
    }
}