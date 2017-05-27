package com.yalantis.phoenix;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

/**
 * @author chenyongkang
 * @Date 2017/5/27 15:18
 */
class BasePullToRefreshData {

    ImageView mContainerView;

    BaseRefreshView mRefreshView;

    float initialMotionY;

    int from;

    float fromDragPercent;

    boolean notify;

    boolean isBeingDragged;

    int currentOffset;

    float currentDragPercent;

    boolean enableToRefresh;

    boolean isRefreshing;

    public BasePullToRefreshData(Context mContext){
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
        return initialMotionY;
    }

    public void setInitialMotionY(float mInitialMotionY) {
        this.initialMotionY = mInitialMotionY;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int mFrom) {
        this.from = mFrom;
    }

    public float getFromDragPercent() {
        return fromDragPercent;
    }

    public void setFromDragPercent(float fromDragPercent) {
        this.fromDragPercent = fromDragPercent;
    }

    public boolean isEnableToRefresh() {
        return enableToRefresh;
    }

    public void setEnableToRefresh(boolean enableToRefresh) {
        this.enableToRefresh = enableToRefresh;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public void setIsRefreshing(boolean isRrefreshing) {
        this.isRefreshing = isRrefreshing;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isBeingDragged() {
        return isBeingDragged;
    }

    public void setBeingDragged(boolean beingDragged) {
        isBeingDragged = beingDragged;
    }

    public int getCurrentOffset() {
        return currentOffset;
    }

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }

    public float getCurrentDragPercent() {
        return currentDragPercent;
    }

    public void setCurrentDragPercent(float currentDragPercent) {
        this.currentDragPercent = currentDragPercent;
    }

//    public int getTotalDragDistance() {
//        return totalDragDistance;
//    }
//
//    public void setTotalDragDistance(int totalDragDistance) {
//        this.totalDragDistance = totalDragDistance;
//    }
}
