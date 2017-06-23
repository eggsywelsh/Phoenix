package com.yalantis.phoenix;

import android.content.Context;
import android.widget.ImageView;

import com.yalantis.phoenix.refresh_view.SuperRefreshView;

/**
 * Pull to refresh's data
 *
 * @author chenyongkang
 * @Date 2017/5/27 15:18
 */
class BasePullToRefreshData {

    /**
     * Wrapper animation drawable or wrapper a common drawable
     */
    ImageView mContainerView;

    SuperRefreshView mRefreshViewAnimate;

    /**
     * record the position where the animation start
     */
    int mFrom;

    /**
     * record the percent which is current drag
     */
    float mFromDragPercent;

    /**
     * Whether is notify the {@link PullToRefreshView.OnRefreshListener} to refreshing
     */
    boolean mNotify;

    /**
     * set current refresh view is enable to refresh
     */
    boolean mEnableToRefresh;

    /**
     * Whether the view is refreshing
     * true is refreshing
     * false is not refreshing
     */
    boolean mIsRefreshing;

    PullToRefreshView.OnRefreshListener mOnRefreshListener;

    public BasePullToRefreshData(Context mContext) {
        this.mContainerView = new ImageView(mContext);
    }

    public ImageView getContainerView() {
        return mContainerView;
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setIsRefreshing(boolean isRrefreshing) {
        this.mIsRefreshing = isRrefreshing;
    }

    public void setOnRefreshListener(PullToRefreshView.OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }
}
