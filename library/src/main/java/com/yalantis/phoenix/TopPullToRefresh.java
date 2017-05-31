package com.yalantis.phoenix;

import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.yalantis.phoenix.refresh_view.BaseRefreshView;

import static com.yalantis.phoenix.PullToRefreshView.MAX_OFFSET_ANIMATION_DURATION;

/**
 * Top refresh component
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
        if (mIsRefreshing != refreshing) {
            setRefreshing(refreshing, false /* mNotify */);
        }
    }

    @Override
    public void setOnRefreshListener(PullToRefreshView.OnRefreshListener listener) {
        mOnRefreshListener = listener;
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
        if (mIsRefreshing != refreshing) {
            super.mNotify = notify;
            mTarget.ensureTarget();
            super.mIsRefreshing = refreshing;
            if (mIsRefreshing) {
                mRefreshView.setPercent(1f, true);
                animateOffsetToCorrectPosition();
            } else {
                animateOffsetToStartPosition();
            }
        }
    }

    @Override
    public void animateOffsetToStartPosition() {
        mFrom = mTarget.getCurrentOffsetTop();
        mFromDragPercent = mTarget.getCurrentDragPercent();
        long animationDuration = Math.abs((long) (MAX_OFFSET_ANIMATION_DURATION * mFromDragPercent));

        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(animationDuration);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mContainerView.clearAnimation();
        mContainerView.startAnimation(mAnimateToStartPosition);
    }

    @Override
    public void animateOffsetToCorrectPosition() {
        mFrom = mTarget.getCurrentOffsetTop();
        mFromDragPercent = mTarget.getCurrentDragPercent();

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mContainerView.clearAnimation();
        mContainerView.startAnimation(mAnimateToCorrectPosition);

        if (mIsRefreshing) {
            mRefreshView.start();
            if (mNotify) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        } else {
            mRefreshView.stop();
            animateOffsetToStartPosition();
        }
        mTarget.updatePaddingAndOffset();
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = mTarget.getTotalTopDragDistance();
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTargetViewTop();

            mTarget.setCurrentDragPercent(mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime);
            mRefreshView.setPercent(mTarget.getCurrentDragPercent(), false);

            offsetTopAndBottom(offset, false);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetTop - mTarget.getTargetViewTop();

        mTarget.setCurrentDragPercent(targetPercent);
        mRefreshView.setPercent(mTarget.getCurrentDragPercent(), true);

        mTarget.moveToStart(targetTop);

        offsetTopAndBottom(offset, false);
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

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    @Override
    public void updateRefreshViewLayout(int left, int top, int right, int bottom) {
        int height = 0;
        int width = 0;
        if (mParent != null) {
            height = mParent.getMeasuredHeight();
            width = mParent.getMeasuredWidth();
        }
        mContainerView.layout(left, top, left + width - right, top + height - bottom);
    }

    public void offsetTopAndBottom(int offset, boolean requiresUpdate) {
        mTarget.offsetTopAndBottom(offset);
        mRefreshView.offsetTopAndBottom(offset);
        mTarget.updateCurrentOffSetTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            mParent.invalidate();
        }
    }
}
