package com.yalantis.phoenix;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 滚动的目标
 *
 * @author chenyongkang
 * @Date 2017/5/27 17:05
 */
final class Target {

    private ViewGroup parent;

    private int mTargetPaddingTop;
    private int mTargetPaddingBottom;
    private int mTargetPaddingRight;
    private int mTargetPaddingLeft;

    private int mCurrentOffsetTop;

    private int mTotalTopDragDistance;

    private int mTotalBottomDragDistance;

    /**
     * 除了头和尾部之外，真正滚动的目标视图
     */
    private View target;

    public Target(ViewGroup parent) {
        this.parent = parent;
    }

    public View getTarget() {
        return target;
    }

    public int getTotalTopDragDistance() {
        return mTotalTopDragDistance;
    }

    public int getCurrentOffsetTop(){
        return mCurrentOffsetTop;
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

    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        target.measure(widthMeasureSpec, heightMeasureSpec);
    }

    boolean isExist() {
        return target != null;
    }

    void ensureTarget() {
        if (target != null)
            return;
        if (parent != null && parent.getChildCount() > 0) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                if (!(child instanceof ImageView)) {
                    target = child;
                    mTargetPaddingBottom = target.getPaddingBottom();
                    mTargetPaddingLeft = target.getPaddingLeft();
                    mTargetPaddingRight = target.getPaddingRight();
                    mTargetPaddingTop = target.getPaddingTop();
                    break;
                }
            }
        }
    }

    void setPadding(int left, int top, int right, int bottom){
        if(target!=null){
            target.setPadding(left,top,right,bottom);
        }
    }

    void update(){
        updateCurrentOffSetTop();
        target.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalTopDragDistance);
    }

    void updateCurrentOffSetTop(){
        mCurrentOffsetTop = target.getTop();
    }
}
