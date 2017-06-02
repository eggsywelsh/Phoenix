package com.yalantis.phoenix;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.yalantis.phoenix.refresh_view.SuperRefreshView;

import static com.yalantis.phoenix.PullToRefreshView.MAX_OFFSET_ANIMATION_DURATION;

/**
 * Top refresh component
 *
 * @author chenyongkang
 * @Date 2017/5/27 15:10
 */
final class BottomPullToRefresh extends BasePullToRefreshData implements BasePullToRefresh {

    private static final String TAG = PullToRefreshView.class.getSimpleName();

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    static final int DRAG_MAX_DISTANCE = 120;

    DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

    Context mContext;

    Target mTarget;

    ViewGroup mParent;

    public BottomPullToRefresh(Context context, ViewGroup parent, Target target) {
        super(context);
        this.mContext = context;
        this.mTarget = target;
        this.mParent = parent;
    }

    @Override
    public void setRefreshView(SuperRefreshView refreshView) {
        mRefreshViewAnimate = refreshView;
//        if (refreshView != null) {
//            mContainerView.setImageDrawable(refreshView.getRefreshDrawable());
//            mContainerView.getDrawable()
//        }
    }

    @Override
    public boolean hasRefreshView() {
        return mRefreshViewAnimate != null;
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
        mRefreshViewAnimate.setPercent(percent, invalidate);
    }

    @Override
    public void setRefreshing(boolean refreshing, boolean notify) {
        if (mIsRefreshing != refreshing) {
            super.mNotify = notify;
            mTarget.ensureTarget();
            super.mIsRefreshing = refreshing;
            if (mIsRefreshing) {
                mRefreshViewAnimate.setPercent(1f, true);
                animateOffsetToCorrectPosition();
            } else {
                animateOffsetToStartPosition();
            }
        }
    }

    @Override
    public void animateOffsetToStartPosition() {
        mFrom = mTarget.getCurrentOffsetBottom();
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
        mFrom = mTarget.getCurrentOffsetBottom();
        mFromDragPercent = mTarget.getCurrentDragPercent();

        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(MAX_OFFSET_ANIMATION_DURATION);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mContainerView.clearAnimation();
        mContainerView.startAnimation(mAnimateToCorrectPosition);

        if (mIsRefreshing) {
            mRefreshViewAnimate.start();
            if (mNotify) {
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        } else {
            mRefreshViewAnimate.stop();
            animateOffsetToStartPosition();
        }
        mTarget.updatePaddingAndOffset();
    }

    private final Animation mAnimateToCorrectPosition = new Animation() {

        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = mTarget.getTotalBottomDragDistance();
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTargetViewTop();

            mTarget.setCurrentDragPercent(mFromDragPercent - (mFromDragPercent - 1.0f) * interpolatedTime);
            mRefreshViewAnimate.setPercent(mTarget.getCurrentDragPercent(), false);

            offsetTopAndBottom(offset, false);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetBottom = mFrom - (int) (mFrom * interpolatedTime);
        float targetPercent = mFromDragPercent * (1.0f - interpolatedTime);
        int offset = targetBottom - mTarget.getTargetViewTop();

        mTarget.setCurrentDragPercent(targetPercent);
        mRefreshViewAnimate.setPercent(mTarget.getCurrentDragPercent(), true);

//        mTarget.moveToStart(targetBottom);

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
            mRefreshViewAnimate.stop();
//            mTarget.updateCurrentOffSetTop();
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
//        int height = 0;
//        int width = 0;
//        if (mParent != null) {
//            height = mParent.getMeasuredHeight();
//            width = mParent.getMeasuredWidth();
//        }
//        mContainerView.layout(left, height-100, left + width - right, height);

        mContainerView.layout(left, top, right, bottom);
    }

    public void offsetTopAndBottom(int offset, boolean requiresUpdate) {
        mTarget.offsetTopAndBottom(offset);
        if (mContainerView.getDrawable() == null && mRefreshViewAnimate != null) {
            mContainerView.setImageDrawable(mRefreshViewAnimate.getRefreshDrawable());
            mContainerView.scrollTo(0, -mTarget.getTotalBottomDragDistance());
            Log.d(TAG, "init offsetTopAndBottom");
        }
        Log.d(TAG, "offsetTopAndBottom " + (Math.abs(mTarget.getCurrentOffsetBottom()) - mTarget.getTotalBottomDragDistance()));
//        mRefreshViewAnimate.offsetTopAndBottom(10);
         mRefreshViewAnimate.offsetTopAndBottom(Math.abs(mTarget.getCurrentOffsetBottom()) - mTarget.getTotalBottomDragDistance());
//        mContainerView.scrollTo(0, offset);
//        mContainerView.scrollTo(0, Math.abs(mTarget.getCurrentOffsetBottom()) - mTarget.getTotalBottomDragDistance());
        mTarget.updateCurrentOffsetBottom(offset);
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            mParent.invalidate();
        }
    }
}
