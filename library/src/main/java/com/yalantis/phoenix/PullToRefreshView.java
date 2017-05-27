package com.yalantis.phoenix;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

public class PullToRefreshView extends ViewGroup {

    private static final float TOP_DRAG_RATE = .5f;
    private static final float BOTTOM_DRAG_RATE = .5f;

    public static final int STYLE_SUN = 0;
    public static final int MAX_OFFSET_ANIMATION_DURATION = 700;

    private static final int INVALID_POINTER = -1;

    //    private View mTarget;
    //    private ImageView mTopRefreshView;
//    private ImageView mBottomRefreshView;
//    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
//    private int mTotalTopDragDistance;
//    private int mTotalBottomDragDistance;

    //    private BaseRefreshView mBaseTopRefreshView;
//    private BaseRefreshView mBaseBottomRefreshView;
    private float mCurrentDragPercent;
//    private int mCurrentOffsetTop;
    private int mActivePointerId;
    private boolean mIsBeingDownDragged;
    private float mInitialMotionY;
    private float mFromDragPercent;
    private OnRefreshListener mListener;

    private boolean mIsBeingUpDragged;

    private Target mTarget;

    /**
     * ====== view attr ======
     */
    private int type;
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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshView);
        type = a.getInteger(R.styleable.RefreshView_type, STYLE_SUN);
        mIsPullTopToRefresh = a.getBoolean(R.styleable.RefreshView_isPullTopToRefresh, true);
        mIsPullBottomToRefresh = a.getBoolean(R.styleable.RefreshView_isPullBottomToRefresh, true);
        mTotalTopDragDistance = a.getDimensionPixelSize(R.styleable.RefreshView_totalTopDragDistance, TopPullToRefresh.DRAG_MAX_DISTANCE);
        mTotalBottomDragDistance = a.getDimensionPixelSize(R.styleable.RefreshView_totalBottomDragDistance, BottomPullToRefresh.DRAG_MAX_DISTANCE);
        a.recycle();

        mTarget = new Target(this);
        mTarget.setTotalTopDragDistance(mTotalTopDragDistance);
        mTarget.setTotalBottomDragDistance(mTotalBottomDragDistance);

//        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        if (mIsPullTopToRefresh) {
            mCompTopToRefresh = new TopPullToRefresh(context,mTarget);
            addView(mCompTopToRefresh.getContainerView());
        }

        if (mIsPullBottomToRefresh) {
            mCompBottomToRefresh = new BottomPullToRefresh(context);
            addView(mCompBottomToRefresh.getContainerView(), getChildCount());
        }

        setWillNotDraw(false);
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
    }

    public void setTopRefreshView(BaseRefreshView view) {
        if (mIsPullTopToRefresh) {
            mCompTopToRefresh.setRefreshView(view);
        }
    }

    public void setBottomRefreshView(BaseRefreshView view) {
        if (mIsPullBottomToRefresh) {
            mCompBottomToRefresh.setRefreshView(view);
        }
    }

//    public void setRefreshStyle(int type) {
//        setRefreshing(false);
//        switch (type) {
//            case STYLE_SUN:
//                mBaseTopRefreshView = new SunRefreshView(getContext(), this);
//                break;
//            default:
//                throw new InvalidParameterException("Type does not exist");
//        }
//        mTopRefreshView.setImageDrawable(mBaseTopRefreshView);
//    }

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
        return mCompTopToRefresh != null ? mCompTopToRefresh.getTotalDragDistance() : 0;
    }

    public int getBottomTotalDragDistance() {
        return mCompBottomToRefresh != null ? mCompBottomToRefresh.getTotalDragDistance() : 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mTarget.ensureTarget();

        if (mTarget == null || !mTarget.isExist())
            return;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);

        if (mIsPullTopToRefresh) {
            mCompTopToRefresh.measureContainerView(widthMeasureSpec, heightMeasureSpec);
        }

        if (mIsPullBottomToRefresh) {
            mCompBottomToRefresh.measureContainerView(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isEnabled() || canChildScrollUp() || (mCompTopToRefresh == null || mCompTopToRefresh.isRefreshing())) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTop(0, true);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDownDragged = false;
                mIsBeingUpDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
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
                if (yDiff >= 0 && mCompTopToRefresh!=null && mCompTopToRefresh.hasRefreshView()) {  // pull down
                    final float scrollTop = yDiff * TOP_DRAG_RATE;
                    mCurrentDragPercent = scrollTop / mTarget.getTotalTopDragDistance();
                    if (mCurrentDragPercent < 0) {
                        return false;
                    }
                    float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));

                    float extraOS = Math.abs(scrollTop) - mTarget.getTotalTopDragDistance();

                    float slingshotDist = mTarget.getTotalTopDragDistance();

                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);

                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;

                    float extraMove = (slingshotDist) * tensionPercent / 2;
                    int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);

                    mCompTopToRefresh.setRefreshViewPercent(mCurrentDragPercent, true);
                    setTargetOffsetTop(targetY - mTarget.getCurrentOffsetTop(), true);
                } else {  // pull up
                    final float scrollBottom = yDiff * BOTTOM_DRAG_RATE;

                    mCurrentDragPercent = scrollBottom / mTarget.getTotalBottomDragDistance();

                    if (mCurrentDragPercent > 0) {
                        return false;
                    }

                    float boundedDragPercent = Math.max(-1f, mCurrentDragPercent);

                    float extraOS = Math.abs(scrollBottom) - mTarget.getTotalBottomDragDistance();

                    float slingshotDist = mTarget.getTotalBottomDragDistance();

                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);

                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;

                    float extraMove = (slingshotDist) * tensionPercent / 2;
                    int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);

                    mCompBottomToRefresh.setRefreshViewPercent(Math.abs(mCurrentDragPercent), true);
                    setTargetOffsetBottom(targetY - mTarget.getTotalBottomDragDistance(), true);
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
                if (y > 0) {  // pull down
                    final float overScrollTop = (yDiff) * TOP_DRAG_RATE;
                    mIsBeingDownDragged = false;
//                    mIsBeingUpDragged = false;
                    if (overScrollTop > mTarget.getTotalTopDragDistance()) {
                        mCompTopToRefresh.setRefreshing(true, true);
                    } else {
                        mCompTopToRefresh.setIsRefreshing(false);
                        mCompTopToRefresh.animateOffsetToStartPosition();
                    }
                } else {  // pull up
                    final float overScrollBottom = (yDiff) * BOTTOM_DRAG_RATE;
//                    mIsBeingDownDragged = false;
                    mIsBeingUpDragged = false;
                    if (Math.abs(overScrollBottom) > mTarget.getTotalBottomDragDistance()) {
                        mCompBottomToRefresh.setRefreshing(true, true);
                    } else {
                        mCompBottomToRefresh.setIsRefreshing(false);
                        mCompBottomToRefresh.animateOffsetToStartPosition();
                    }
                }

                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

//    private void animateOffsetToStartPosition() {
//
//    }

   /* private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mFromDragPercent = mCurrentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mTopRefreshView.clearAnimation();
        mTopRefreshView.startAnimation(mAnimateToCorrectPosition);

        if (mTopRefreshing) {
            mBaseTopRefreshView.start();
            if (mNotify) {
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        } else {
            mBaseTopRefreshView.stop();
            animateOffsetToStartPosition();
        }
        mCurrentOffsetTop = mTarget.getTop();
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTotalTopDragDistance);
    }*/

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

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
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ensureTarget();
        if (mTarget == null)
            return;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
        mTopRefreshView.layout(left, top, left + width - right, top + height - bottom);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}

