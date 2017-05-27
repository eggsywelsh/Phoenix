package com.yalantis.phoenix;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

import static com.yalantis.phoenix.PullToRefreshView.MAX_OFFSET_ANIMATION_DURATION;

/**
 * 顶部刷新控件类
 *
 * @author chenyongkang
 * @Date 2017/5/27 15:10
 */
final class TopPullToRefresh extends BasePullToRefreshData implements BasePullToRefresh {

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    static final int DRAG_MAX_DISTANCE = 120;

    DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    Context mContext;

    Target mTarget;

    ViewGroup mParent;

    public TopPullToRefresh(Context context, ViewGroup parent, Target target) {
        super(context);
        this.mContext = context;
        this.mTarget = target;
        this.mParent = parent;
    }

    @Override
    public void setRefreshView(BaseRefreshView refreshView) {
        mRefreshView = refreshView;
        if (refreshView != null) {
            mContainerView.setImageDrawable(refreshView);
        }
    }

    @Override
    public boolean hasRefreshView() {
        return mRefreshView != null;
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (isRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    @Override
    public void setOnRefreshListener(PullToRefreshView.OnRefreshListener listener) {

    }

    @Override
    public void setContainerViewPadding(int left, int top, int right, int bottom) {
        if (mContainerView != null) {
            mContainerView.setPadding(left, top, right, bottom);
        }
    }

    @Override
    public void measureContainerView(int widthMeasureSpec, int heightMeasureSpec) {
        mContainerView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setRefreshViewPercent(float percent, boolean invalidate) {
        mRefreshView.setPercent(percent, invalidate);
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notify) {
        if (isRefreshing != refreshing) {
            super.notify = notify;
            mTarget.ensureTarget();
            super.isRefreshing = refreshing;
            if (isRefreshing) {
                mRefreshView.setPercent(1f, true);
                animateOffsetToCorrectPosition();
            } else {
                animateOffsetToStartPosition();
            }
        }
    }

    @Override
    public void animateOffsetToStartPosition() {
        from = mTarget.getCurrentOffsetTop();
        fromDragPercent = currentDragPercent;
        long animationDuration = Math.abs((long) (MAX_OFFSET_ANIMATION_DURATION * fromDragPercent));

        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(animationDuration);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    @Override
    public void animateOffsetToCorrectPosition() {
        from = mTarget.getCurrentOffsetTop();
        fromDragPercent = currentDragPercent;

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mTopRefreshView.clearAnimation();
        mTopRefreshView.startAnimation(mAnimateToCorrectPosition);

        if (isRefreshing) {
            mRefreshView.start();
            if (notify) {
                if (mListener != null) {
                    mListener.onRefresh();
                }
            }
        } else {
            mRefreshView.stop();
            animateOffsetToStartPosition();
        }
        mTarget.update();
    }


    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = mTarget.getTotalTopDragDistance();
            targetTop = (from + (int) ((endTarget - from) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();

            currentDragPercent = fromDragPercent - (fromDragPercent - 1.0f) * interpolatedTime;
            mRefreshView.setPercent(currentDragPercent, false);

            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        mTarget.offsetTopAndBottom(offset);
        mBaseTopRefreshView.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            parent.invalidate();
        }
    }

    private void moveToStart(float interpolatedTime) {
        int targetTop = from - (int) (from * interpolatedTime);
        float targetPercent = fromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mTarget.getTop();

        currentDragPercent = targetPercent;
        mRefreshView.setTopPercent(mCurrentDragPercent, true);
        mTarget.setPadding(mTargetPaddingLeft, mTargetPaddingTop, mTargetPaddingRight, mTargetPaddingBottom + targetTop);
        setTargetOffsetTop(offset, false);
    }

    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mRefreshView.stop();
            mTarget.updateCurrentOffSetTop();
        }
    };


}
