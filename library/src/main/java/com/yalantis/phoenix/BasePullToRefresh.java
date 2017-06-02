package com.yalantis.phoenix;

import android.widget.ImageView;

import com.yalantis.phoenix.refresh_view.SuperRefreshView;

/**
 * base pull to refresh interface
 *
 * @author chenyongkang
 * @Date 2017/5/27 14:58
 */
public interface BasePullToRefresh {

    /**
     * return container view
     *
     * @return
     */
    ImageView getContainerView();

    /**
     * set the refresh view
     *
     * @param refreshView
     */
    void setRefreshView(SuperRefreshView refreshView);

    /**
     * set is refreshing,and mNotify listener if exists
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

    /**
     * updatePaddingAndOffset refresh view's layout
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    void updateRefreshViewLayout(int left,int top,int right,int bottom);

    /**
     * set the offset top and bottom
     * @param offset
     */
    void offsetTopAndBottom(int offset,boolean requiresUpdate);
}
