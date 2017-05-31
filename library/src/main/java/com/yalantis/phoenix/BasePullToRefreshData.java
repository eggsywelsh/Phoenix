package com.yalantis.phoenix;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

/**
 * Pull to refresh data
 *
 * @author chenyongkang
 * @Date 2017/5/27 15:18
 */
class BasePullToRefreshData {

    ImageView mContainerView;

    BaseRefreshView mRefreshView;

    float mInitialMotionY;

    int mFrom;

    float mFromDragPercent;

    boolean mNotify;

    boolean mIsBeingDragged;

    int mCurrentOffset;

    boolean mEnableToRefresh;

    boolean mIsRefreshing;

    PullToRefreshView.OnRefreshListener mOnRefreshListener;

    public BasePullToRefreshData(Context mContext) {
        this.mContainerView = new ImageView(mContext);
    }

    public View getContainerView() {
        return mContainerView;
    }

    public void setContainerView(ImageView mContainerView) {
        this.mContainerView = mContainerView;
    }

    public BaseRefreshView getRefreshView() {
        return mRefreshView;
    }

    public void setRefreshView(BaseRefreshView mRefreshView) {
        this.mRefreshView = mRefreshView;
    }

    public float getInitialMotionY() {
        return mInitialMotionY;
    }

    public void setInitialMotionY(float mInitialMotionY) {
        this.mInitialMotionY = mInitialMotionY;
    }

    public int getFrom() {
        return mFrom;
    }

    public void setFrom(int mFrom) {
        this.mFrom = mFrom;
    }

    public float getFromDragPercent() {
        return mFromDragPercent;
    }

    public void setFromDragPercent(float mFromDragPercent) {
        this.mFromDragPercent = mFromDragPercent;
    }

    public boolean isEnableToRefresh() {
        return mEnableToRefresh;
    }

    public void setEnableToRefresh(boolean enableToRefresh) {
        this.mEnableToRefresh = enableToRefresh;
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setIsRefreshing(boolean isRrefreshing) {
        this.mIsRefreshing = isRrefreshing;
    }

    public boolean isNotify() {
        return mNotify;
    }

    public void setNotify(boolean notify) {
        this.mNotify = notify;
    }

    public boolean isBeingDragged() {
        return mIsBeingDragged;
    }

    public void setBeingDragged(boolean beingDragged) {
        mIsBeingDragged = beingDragged;
    }

    public int getmCurrentOffset() {
        return mCurrentOffset;
    }

    public void setmCurrentOffset(int mCurrentOffset) {
        this.mCurrentOffset = mCurrentOffset;
    }

    public PullToRefreshView.OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    public void setOnRefreshListener(PullToRefreshView.OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }
}
