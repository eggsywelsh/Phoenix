package com.yalantis.phoenix;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.yalantis.phoenix.refresh_view.MoreRefreshView;
import com.yalantis.phoenix.refresh_view.SuperRefreshView;
import com.yalantis.phoenix.refresh_view.SunRefreshView;
import com.yalantis.phoenix.util.Utils;

public class PullToRefreshView extends ViewGroup {

    public static final String TAG = "PullToRefreshView";

    private static final float TOP_DRAG_RATE = .5f;
    private static final float BOTTOM_DRAG_RATE = .5f;

    public static final int STYLE_SUN = 0;
    public static final int MAX_OFFSET_ANIMATION_DURATION = 700;

    private static final int INVALID_POINTER = -1;

    private int mTouchSlop;
    private int mTotalTopDragDistance;
    private int mTotalBottomDragDistance;

    private int mTopRefreshHeight;
    private int mBottomRefreshHeight;

    private int mActivePointerId;
    private boolean mIsBeingDownDragged;
    private float mInitialMotionY;

    private boolean mIsBeingUpDragged;

    private Target mTarget;

    /**
     * ====== view attr ======
     */
    // set has pull top to refresh
    private boolean mIsPullTopToRefresh;
    // set has pull bottom to refresh
    private boolean mIsPullBottomToRefresh;

    TopPullToRefresh mCompTopToRefresh;

    BottomPullToRefresh mCompBottomToRefresh;


    public PullToRefreshView(Context context) {
        this(context, null);
    }

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshView);

        mIsPullTopToRefresh = a.getBoolean(R.styleable.PullToRefreshView_isPullTopToRefresh, false);
        mIsPullBottomToRefresh = a.getBoolean(R.styleable.PullToRefreshView_isPullBottomToRefresh, false);

        mTotalTopDragDistance = a.getDimensionPixelSize(R.styleable.PullToRefreshView_totalTopDragDistance,
                Utils.convertDpToPixel(context, TopPullToRefresh.DRAG_MAX_DISTANCE));
        mTotalBottomDragDistance = a.getDimensionPixelSize(R.styleable.PullToRefreshView_totalBottomDragDistance,
                Utils.convertDpToPixel(context, BottomPullToRefresh.DRAG_MAX_DISTANCE));

        mTopRefreshHeight = a.getDimensionPixelSize(R.styleable.PullToRefreshView_topRefreshHeight,
                Utils.convertDpToPixel(context, TopPullToRefresh.DRAG_MAX_DISTANCE));
        mBottomRefreshHeight = a.getDimensionPixelSize(R.styleable.PullToRefreshView_bottomRefreshHeight,
                Utils.convertDpToPixel(context, BottomPullToRefresh.DRAG_MAX_DISTANCE));

        a.recycle();

        mTarget = new Target(this);
        mTarget.setTotalTopDragDistance(mTotalTopDragDistance);
        mTarget.setTotalBottomDragDistance(mTotalBottomDragDistance);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        if (mIsPullTopToRefresh) {
            mCompTopToRefresh = new TopPullToRefresh(context, this, mTarget);
            setTopRefreshView(new SunRefreshView(context, this, mCompTopToRefresh.getContainerView()));
            addView(mCompTopToRefresh.getContainerView());
        }

        if (mIsPullBottomToRefresh) {
            mCompBottomToRefresh = new BottomPullToRefresh(context, this, mTarget);
            MoreRefreshView moreRefreshView = new MoreRefreshView(context, this, mCompTopToRefresh.getContainerView());
            moreRefreshView.setAnimationDrawable((AnimationDrawable) context.getResources().getDrawable(R.drawable.list_bottom_load_more));
            setBottomRefreshView(moreRefreshView);
            addView(mCompBottomToRefresh.getContainerView(), getChildCount());
        }

        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    public void setTopRefreshView(SuperRefreshView view) {
        if (mIsPullTopToRefresh && mCompTopToRefresh != null) {
            mCompTopToRefresh.setRefreshView(view);
        }
    }

    public void setBottomRefreshView(SuperRefreshView view) {
        if (mIsPullBottomToRefresh) {
            mCompBottomToRefresh.setRefreshView(view);
        }
    }

    /**
     * This method sets padding for the top refresh (progress) view.
     */
    public void setTopRefreshViewPadding(int left, int top, int right, int bottom) {
        if (mCompTopToRefresh != null) {
            mCompTopToRefresh.setContainerViewPadding(left, top, right, bottom);
        }
    }

    /**
     * This method sets padding for the bottom refresh (progress) view.
     */
    public void setBottomRefreshViewPadding(int left, int top, int right, int bottom) {
        if (mCompBottomToRefresh != null) {
            mCompBottomToRefresh.setContainerViewPadding(left, top, right, bottom);
        }
    }

    public int getTopTotalDragDistance() {
        return mTarget != null ? mTarget.getTotalTopDragDistance() : 0;
    }

    public int getBottomTotalDragDistance() {
        return mTarget != null ? mTarget.getTotalBottomDragDistance() : 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mTarget == null || !mTarget.isExist())
            return;

        mTarget.ensureTarget();

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);

        if (mIsPullTopToRefresh && mCompTopToRefresh != null) {
            mCompTopToRefresh.measureContainerView(widthMeasureSpec, heightMeasureSpec);
        }

        if (mIsPullBottomToRefresh && mCompBottomToRefresh != null) {
            mCompBottomToRefresh.measureContainerView(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        mTarget.ensureTarget();
        if (mTarget == null)
            return;

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.updateLayout(left, top, right, bottom);

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        if (mCompTopToRefresh != null) {
            mCompTopToRefresh.updateRefreshViewLayout(left, top, left + width - right, (top + height - bottom));
        }

        if (mCompBottomToRefresh != null) {
//            mCompBottomToRefresh.updateRefreshViewLayout(left, top, left + width - right, (top + height - bottom));
            mCompBottomToRefresh.updateRefreshViewLayout(left, (top + height - bottom) - mTotalBottomDragDistance, left + width - right, (top + height - bottom));
//            mCompBottomToRefresh.updateRefreshViewLayout(left, (top + height - bottom) , left + width - right, (top + height - bottom)+100);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isEnabled()
                || (!mIsPullTopToRefresh && mActivePointerId != INVALID_POINTER && ev.getAction() == MotionEvent.ACTION_MOVE && getMotionEventY(ev, mActivePointerId) - mInitialMotionY > 0)
                || (!mIsPullBottomToRefresh && mActivePointerId != INVALID_POINTER && ev.getAction() == MotionEvent.ACTION_MOVE && getMotionEventY(ev, mActivePointerId) - mInitialMotionY < 0)
                || (mActivePointerId != INVALID_POINTER && ev.getAction() == MotionEvent.ACTION_MOVE && getMotionEventY(ev, mActivePointerId) - mInitialMotionY > 0 && canChildScrollUp())
                || (mActivePointerId != INVALID_POINTER && ev.getAction() == MotionEvent.ACTION_MOVE && getMotionEventY(ev, mActivePointerId) - mInitialMotionY < 0 && canChildScrollDown())
                || (mCompTopToRefresh != null && mCompTopToRefresh.isRefreshing())
                || (mCompBottomToRefresh != null && mCompBottomToRefresh.isRefreshing())
                ) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mCompTopToRefresh != null) {
                    mCompTopToRefresh.offsetTopAndBottom(0, true);
                }

                if (mCompBottomToRefresh != null) {
                    mCompBottomToRefresh.offsetTopAndBottom(0, true);
                }

                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDownDragged = false;
                mIsBeingUpDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
//                mTarget.offsetTopAndBottom(5);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
                if (yDiff > mTouchSlop && !mIsBeingDownDragged) {
                    mIsBeingDownDragged = true;
                } else if (yDiff < 0 && Math.abs(yDiff) > mTouchSlop && !mIsBeingUpDragged) {
                    mIsBeingUpDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mInitialMotionY = 0;
                mIsBeingDownDragged = false;
                mIsBeingUpDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        return mIsBeingDownDragged || mIsBeingUpDragged;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {

        if (!mIsBeingDownDragged && !mIsBeingUpDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;
                if (yDiff >= 0 && mCompTopToRefresh != null && mCompTopToRefresh.hasRefreshView()) {  // pull down
                    final float scrollTop = yDiff * TOP_DRAG_RATE;

                    mTarget.setCurrentDragPercent(scrollTop / mTarget.getTotalTopDragDistance());

                    if (mTarget.getCurrentDragPercent() < 0) {
                        return false;
                    }

                    float boundedDragPercent = Math.min(1f, Math.abs(mTarget.getCurrentDragPercent()));

                    float extraOS = Math.abs(scrollTop) - mTarget.getTotalTopDragDistance();

                    float slingshotDist = mTarget.getTotalTopDragDistance();

                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);

                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;

                    float extraMove = (slingshotDist) * tensionPercent / 2;
                    int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);
                    Log.d(TAG, "targetY " + targetY);
                    mCompTopToRefresh.setRefreshViewPercent(mTarget.getCurrentDragPercent(), true);
                    Log.d(TAG, " pull down , offset " + (targetY - mTarget.getCurrentOffsetTop()));
                    mCompTopToRefresh.offsetTopAndBottom(targetY - mTarget.getCurrentOffsetTop(), true);
                } else if (yDiff < 0 && mCompBottomToRefresh != null && mCompBottomToRefresh.hasRefreshView()) {  // pull up
                    final float scrollBottom = yDiff * BOTTOM_DRAG_RATE;

                    mTarget.setCurrentDragPercent(scrollBottom / mTarget.getTotalBottomDragDistance());

                    if (mTarget.getCurrentDragPercent() > 0) {
                        return false;
                    }

                    float boundedDragPercent = Math.max(-1f, mTarget.getCurrentDragPercent());

                    float extraOS = scrollBottom + mTarget.getTotalBottomDragDistance();

                    float slingshotDist = mTarget.getTotalBottomDragDistance();

                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(Math.abs(extraOS), slingshotDist * 2) / slingshotDist);

                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;

                    float extraMove = (slingshotDist) * tensionPercent / 2;
                    int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);
                    Log.d(TAG, "targetY " + targetY);
                    mCompBottomToRefresh.setRefreshViewPercent(mTarget.getCurrentDragPercent(), true);
                    Log.d(TAG, " pull up , offset " + (targetY - mTarget.getCurrentOffsetBottom()));
                    mCompBottomToRefresh.offsetTopAndBottom(targetY - mTarget.getCurrentOffsetBottom(), true);
                }

                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = y - mInitialMotionY;

                mIsBeingDownDragged = false;
                mIsBeingUpDragged = false;

                if (yDiff > 0) {  // pull down
                    final float overScrollTop = (yDiff) * TOP_DRAG_RATE;
                    if (mCompTopToRefresh != null) {
                        if (overScrollTop >= mTarget.getTotalTopDragDistance()) {
                            mCompTopToRefresh.setRefreshing(true, true);
                        } else {
                            mCompTopToRefresh.setIsRefreshing(false);
                            mCompTopToRefresh.animateOffsetToStartPosition();
                        }
                    }
                } else {  // pull up
//                    final float overScrollBottom = (yDiff) * BOTTOM_DRAG_RATE;
//                    if (mCompBottomToRefresh != null) {
//                        if (Math.abs(overScrollBottom) >= mTarget.getTotalBottomDragDistance()) {
//                            mCompBottomToRefresh.setRefreshing(true, true);
//                        } else {
//                            mCompBottomToRefresh.setIsRefreshing(false);
//                            mCompBottomToRefresh.animateOffsetToStartPosition();
//                        }
//                    }
                }

                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget.getTargetView() instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget.getTargetView();
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getTargetScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget.getTargetView(), -1);
        }
    }

    private boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget.getTargetView() instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget.getTargetView();
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getTargetScrollY() < 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget.getTargetView(), 1);
        }
    }

    public void setTopRefreshing(boolean isRefreshing) {
        if (mCompTopToRefresh != null) {
            mCompTopToRefresh.setRefreshing(isRefreshing);
        }
    }

    public void setOnTopRefreshListener(OnRefreshListener listener) {
        if (mCompTopToRefresh != null) {
            mCompTopToRefresh.setOnRefreshListener(listener);
        }
    }

    public void setBottomRefreshing(boolean isRefreshing) {
        if (mCompBottomToRefresh != null) {
            mCompBottomToRefresh.setRefreshing(isRefreshing);
        }
    }

    public void setOnBottomRefreshListener(OnRefreshListener listener) {
        if (mCompBottomToRefresh != null) {
            mCompBottomToRefresh.setOnRefreshListener(listener);
        }
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}

