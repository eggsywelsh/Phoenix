package com.yalantis.phoenix;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * wrapper mTargetView view
 *
 * @author chenyongkang
 * @Date 2017/5/27 17:05
 */
final class Target {

    private static final String TAG = PullToRefreshView.TAG;

    private ViewGroup mParent;

    private int mTargetPaddingTop;
    private int mTargetPaddingBottom;
    private int mTargetPaddingRight;
    private int mTargetPaddingLeft;

    private int mCurrentOffsetTop;
    private int mCurrentOffsetBottom;

    private int mTotalTopDragDistance;

    private int mTotalBottomDragDistance;

    private float mCurrentDragPercent;

    /**
     * the real scroll mTargetView view or view group,beside header and tail
     */
    private View mTargetView;

    public Target(ViewGroup parent) {
        this.mParent = parent;
    }

    public View getTargetView() {
        return mTargetView;
    }

    public int getTotalTopDragDistance() {
        return mTotalTopDragDistance;
    }

    public int getCurrentOffsetTop() {
        return mCurrentOffsetTop;
    }

    public int getCurrentOffsetBottom() {
        return mCurrentOffsetBottom;
    }

    public void setTotalTopDragDistance(int totalDragDistance) {
        this.mTotalTopDragDistance = totalDragDistance;
    }

    public int getTotalBottomDragDistance() {
        return mTotalBottomDragDistance;
    }

    public void setTotalBottomDragDistance(int totalDragDistance) {
        this.mTotalBottomDragDistance = totalDragDistance;
    }

    boolean isExist() {
        return mTargetView != null;
    }

    void ensureTarget() {
        if (mTargetView != null)
            return;
        if (mParent != null && mParent.getChildCount() > 0) {
            for (int i = 0; i < mParent.getChildCount(); i++) {
                View child = mParent.getChildAt(i);
                if (!(child instanceof ImageView)) {
                    mTargetView = child;
                    mTargetPaddingBottom = mTargetView.getPaddingBottom();
                    mTargetPaddingLeft = mTargetView.getPaddingLeft();
                    mTargetPaddingRight = mTargetView.getPaddingRight();
                    mTargetPaddingTop = mTargetView.getPaddingTop();
                    break;
                }
            }
        }
    }

    void measure(int widthMeasureSpec, int heightMeasureSpec) {
        mTargetView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    void updatePadding(int left, int top, int right, int bottom) {
        if (mTargetView != null) {
            mTargetView.setPadding(left, top, right, bottom);
        }
    }

    void updatePaddingAndOffset() {
        updateCurrentOffSetTop();
//        updateCurrentOffsetBottom();
        updatePadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalTopDragDistance);
    }

    void updateCurrentOffSetTop() {
        mCurrentOffsetTop = getTargetViewTop();
        Log.d(TAG, "offset top " + mCurrentOffsetTop);
    }

    void updateCurrentOffsetBottom(int offset) {
        mCurrentOffsetBottom += offset;
        Log.d(TAG,"sum offset bottom "+mCurrentOffsetBottom);
    }

    void updateLayout(int left, int top, int right, int bottom) {
        int height = mParent.getMeasuredHeight();
        int width = mParent.getMeasuredWidth();
        mTargetView.layout(left, top + mCurrentOffsetTop,
                left + width - right,
                top + height - bottom + mCurrentOffsetTop);
    }

    int getTargetScrollY() {
        return mTargetView.getScrollY();
    }

    int getTargetViewTop() {
        return mTargetView != null ? mTargetView.getTop() : 0;
    }

//    int getTargetViewBottom() {
//        return mTargetView != null ? mTargetView.getBottom() : 0;
//    }

    void offsetTopAndBottom(int offset) {
        mTargetView.offsetTopAndBottom(offset);
    }

    void moveToStart(int targetTop) {
        updatePadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
    }

    public float getCurrentDragPercent() {
        return mCurrentDragPercent;
    }

    public void setCurrentDragPercent(float mCurrentDragPercent) {
        this.mCurrentDragPercent = mCurrentDragPercent;
    }
}
