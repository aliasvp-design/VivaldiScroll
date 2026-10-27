package com.example.vivaldiscroll;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

public class ScrollService extends AccessibilityService {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private WindowManager manager;
    private TextView bubble;
    private WindowManager.LayoutParams params;
    private boolean vivaldi = false;
    private boolean scrolling = false;
    private int remaining = 0;
    private boolean up = true;
    private final Runnable next = this::sendNext;

    @Override protected void onServiceConnected() {
        super.onServiceConnected();
        manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        bubble = new TextView(this);
        bubble.setText("↑");
        bubble.setTextSize(25);
        bubble.setTextColor(Color.WHITE);
        bubble.setGravity(Gravity.CENTER);
        GradientDrawable background = new GradientDrawable();
        background.setColor(0xCC263747);
        background.setCornerRadius(100);
        bubble.setBackground(background);
        bubble.setAlpha(0.8f);
        int size = (int)(52 * getResources().getDisplayMetrics().density);
        params = new WindowManager.LayoutParams(size, size,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        params.y = 120;
        bubble.setOnClickListener(v -> startScroll(true));
        bubble.setOnLongClickListener(v -> { startScroll(false); return true; });
        bubble.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            int startY;
            boolean moved;
            @Override public boolean onTouch(View v, MotionEvent e) {
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX=e.getRawX(); downY=e.getRawY(); startY=params.y; moved=false; return false;
                    case MotionEvent.ACTION_MOVE:
                        if (Math.abs(e.getRawY()-downY)>12) {
                            moved=true;
                            params.y=startY+(int)(e.getRawY()-downY);
                            manager.updateViewLayout(bubble,params);
                            return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        return moved;
                    default: return false;
                }
            }
        });
        manager.addView(bubble, params);
        bubble.setVisibility(View.GONE);
    }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (bubble == null) return;
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && event.getEventType() != AccessibilityEvent.TYPE_WINDOWS_CHANGED) return;
        CharSequence name = event.getPackageName();
        if (name == null) return;
        String pkg = name.toString();
        if (pkg.equals(getPackageName()) || pkg.equals("com.android.systemui")) return;
        boolean active = pkg.equals("com.vivaldi.browser") || pkg.equals("com.vivaldi.browser.snapshot");
        if (active != vivaldi) {
            vivaldi = active;
            bubble.setVisibility(active ? View.VISIBLE : View.GONE);
            if (!active) stopScroll();
        }
    }
    private void startScroll(boolean directionUp) {
        if (!vivaldi || scrolling) return;
        up=directionUp; remaining=12; scrolling=true;
        bubble.setText(up ? "↑" : "↓");
        sendNext();
    }
    private void sendNext() {
        if (!scrolling || !vivaldi || remaining-- <= 0) { stopScroll(); return; }
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        float x=width*0.45f;
        float from=height*(up?0.28f:0.78f);
        float to=height*(up?0.78f:0.28f);
        Path path=new Path(); path.moveTo(x,from); path.lineTo(x,to);
        GestureDescription.Builder builder=new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path,0,230));
        boolean sent=dispatchGesture(builder.build(),new GestureResultCallback(){
            @Override public void onCompleted(GestureDescription g) { handler.postDelayed(next,80); }
            @Override public void onCancelled(GestureDescription g) { stopScroll(); }
        },handler);
        if (!sent) stopScroll();
    }
    private void stopScroll() {
        scrolling=false; remaining=0; handler.removeCallbacks(next);
        if (bubble!=null) bubble.setText("↑");
    }
    @Override public void onInterrupt() { stopScroll(); }
    @Override public void onDestroy() {
        stopScroll();
        if (manager!=null && bubble!=null) manager.removeView(bubble);
        super.onDestroy();
    }
}
