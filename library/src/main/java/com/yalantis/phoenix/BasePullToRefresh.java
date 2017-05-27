package com.yalantis.phoenix;

import android.view.View;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

/**
 * @author chenyongkang
 * @Date 2017/5/27 14:58
 */
public interface BasePullToRefresh {

    /**
     * return container view
     *
     * @return
     */
    View getContainerView();

    /**
     * set the refresh view
     *
     * @param refreshView
     */
    void setRefreshView(BaseRefreshView refreshView);

    /**
     * set is refreshing,and notify listener if exists
     *
     * @param refreshing
     * @param notify
     */
    void setRefreshing(boolean refreshing, final boolean notify);

    /**
     * set is refreshing
     *
     * @param refreshing
     */
    void setRefreshing(boolean refreshing);

    /**
     * set refreshing listener
     *
     * @param listener
     */
    void setOnRefreshListener(PullToRefreshView.OnRefreshListener listener);

    /**
     * set container view padding
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    void setContainerViewPadding(int left, int top, int right, int bottom) ;

    /**
     * remeasure container view
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    void measureContainerView(int widthMeasureSpec, int heightMeasureSpec);

    /**
     * set refresh view percent
     */
    void setRefreshViewPercent(float percent, boolean invalidate);

    /**
     * animate to start position
     */
    void animateOffsetToStartPosition();

    /**
     * animate to correct position
     */
    void animateOffsetToCorrectPosition();

    /**
     * has set refresh view
     *
     * @return
     */
    boolean hasRefreshView();
}
