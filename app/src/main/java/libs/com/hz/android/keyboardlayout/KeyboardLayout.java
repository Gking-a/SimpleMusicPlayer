package libs.com.hz.android.keyboardlayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class KeyboardLayout extends FrameLayout {
    private boolean isKeyboardActive = false;

    private int keyboardHeight = 0;

    private KeyboardLayoutListener keyboardLayoutListener;

    public KeyboardLayout(Context context) {
        this(context, (AttributeSet)null, 0);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalLayoutListener());
    }

    public static interface KeyboardLayoutListener {
        void onKeyboardStateChanged(boolean param1Boolean, int param1Int);
    }

    private class KeyboardOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        int screenHeight = 0;

        public void onGlobalLayout() {
            Rect rect = new Rect();
            ((Activity)KeyboardLayout.this.getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            int popUpHeight = screenHeight - rect.bottom;
            boolean isActive = false;
            if (Math.abs(popUpHeight) > screenHeight / 5) {
                isActive = true;
                KeyboardLayout.this.keyboardHeight = popUpHeight;
            }
            KeyboardLayout.this.isKeyboardActive = isActive;
            if (KeyboardLayout.this.keyboardLayoutListener != null)
                KeyboardLayout.this.keyboardLayoutListener.onKeyboardStateChanged(KeyboardLayout.this.isKeyboardActive, KeyboardLayout.this.keyboardHeight);
        }

        private int getScreenHeight() {
            if (this.screenHeight > 0)
                return this.screenHeight;
            WindowManager windowManager = (WindowManager)KeyboardLayout.this.getContext().getSystemService(Context.WINDOW_SERVICE);
            this.screenHeight = windowManager.getDefaultDisplay().getHeight();
            return this.screenHeight;
        }

        private KeyboardOnGlobalLayoutListener() {}
    }

    public int getKeyboardHeight() {
        return this.keyboardHeight;
    }

    public void setKeyboardLayoutListener(KeyboardLayoutListener keyboardLayoutListener) {
        this.keyboardLayoutListener = keyboardLayoutListener;
    }

    public KeyboardLayoutListener getKeyboardLayoutListener() {
        return this.keyboardLayoutListener;
    }

    public boolean isKeyboardActive() {
        return this.isKeyboardActive;
    }
}
