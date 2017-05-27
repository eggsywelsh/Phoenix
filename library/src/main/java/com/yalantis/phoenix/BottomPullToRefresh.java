package com.yalantis.phoenix;

import android.content.Context;
import android.view.View;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

/**
 * 顶部刷新控件类
 *
 * @author chenyongkang
 * @Date 2017/5/27 15:10
 */
final class BottomPullToRefresh extends BasePullToRefreshData implements BasePullToRefresh {

    static final int DRAG_MAX_DISTANCE = 120;

    BasePullToRefreshData data;

    Context mContext;

    public BottomPullToRefresh(Context context){
        super(context);
        this.mContext = context;
    }

    @Override
    public void setRefreshView(BaseRefreshView refreshView) {

    }

    @Override
    public void setRefreshing(boolean refreshing) {

    }

    @Override
    public int getTotalDragDistance() {
        return 0;
    }

    @Override
    public void setOnRefreshListener(PullToRefreshView.OnRefreshListener listener) {

    }

    @Override
    public View getContainerView() {
        return data.mContainerView;
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notify) {

    }

    @Override
    public void setContainerViewPadding(int left, int top, int right, int bottom) {

    }

    @Override
    public void measureContainerView(int widthMeasureSpec, int heightMeasureSpec) {

    }

    @Override
    public void setRefreshViewPercent(float percent, boolean invalidate) {

    }
}
