package com.folioreader.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

public class SwipeRevealLayout extends FrameLayout {

    private View mainView;
    private View secondaryView;

    private ViewDragHelper dragHelper;
    private int touchSlop;

    private int mainOffset = 0;
    private boolean open = false;

    private float downX;
    private float downY;

    public SwipeRevealLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public SwipeRevealLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeRevealLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        dragHelper = ViewDragHelper.create(this, 1.0f, new DragCallback());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() < 2) {
            throw new IllegalStateException("SwipeRevealLayout requires exactly two children");
        }
        secondaryView = getChildAt(0);
        mainView = getChildAt(1);
    }

    private int secondaryWidth() {
        return secondaryView == null ? 0 : secondaryView.getMeasuredWidth();
    }

    private int innerWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mainView == null) return;

        int w = innerWidth();
        int h = mainView.getMeasuredHeight();
        if (h <= 0) {
            h = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        }
        mainView.measure(
                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mainView == null || secondaryView == null) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }

        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int w = innerWidth();

        if (open) {
            mainOffset = -secondaryWidth();
        }

        int mainLeft = pl + mainOffset;
        mainView.layout(mainLeft, pt, mainLeft + w, pt + mainView.getMeasuredHeight());

        int secLeft = mainLeft + w;
        secondaryView.layout(secLeft, pt, secLeft + secondaryView.getMeasuredWidth(),
                pt + secondaryView.getMeasuredHeight());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                dragHelper.shouldInterceptTouchEvent(ev);
                return false;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(ev.getX() - downX);
                float dy = Math.abs(ev.getY() - downY);

                if (dx > touchSlop && dx > dy) {
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                    return dragHelper.shouldInterceptTouchEvent(ev);
                }
                return false;
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();
        }
    }

    public void close(boolean animate) {
        open = false;
        mainOffset = 0;
        if (animate && mainView != null) {
            dragHelper.smoothSlideViewTo(mainView, getPaddingLeft(), getPaddingTop());
            postInvalidateOnAnimation();
        } else {
            requestLayout();
        }
    }

    private class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == mainView;
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_IDLE && getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int minLeft = getPaddingLeft() - secondaryWidth();
            int maxLeft = getPaddingLeft();
            return Math.max(Math.min(left, maxLeft), minLeft);
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return child.getTop();
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return secondaryWidth();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            mainOffset = left - getPaddingLeft();

            int desiredSecLeft = mainView.getRight();
            secondaryView.offsetLeftAndRight(desiredSecLeft - secondaryView.getLeft());
            invalidate();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int secW = secondaryWidth();
            int closedLeft = getPaddingLeft();
            int openedLeft = getPaddingLeft() - secW;

            int settleLeft = closedLeft;
            float minVelocity = dragHelper.getMinVelocity();
            if (xvel < -minVelocity) {
                settleLeft = openedLeft;
            } else if (xvel > minVelocity) {
                settleLeft = closedLeft;
            } else if (releasedChild.getLeft() < closedLeft - secW / 2) {
                settleLeft = openedLeft;
            }

            open = settleLeft == openedLeft;
            dragHelper.settleCapturedViewAt(settleLeft, releasedChild.getTop());
            postInvalidateOnAnimation();
        }
    }
}
