package com.yalantis.phoenix.refresh_view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Base animation refresh view
 *
 * @author chenyongkang
 * @Date 2017/6/5 15:44
 */
public abstract class BaseAnimationRefreshView implements SuperRefreshView {

    private float mPercent = 0.0f;

    Context mContext;

    ImageView mContainerView;

    /**
     * refreshing animation drawable
     */
    AnimationDrawable mAnimationDrawable;

    public BaseAnimationRefreshView(Context context, ImageView containerView, AnimationDrawable animationDrawable) {
        this.mContext = context;
        this.mContainerView = containerView;
        this.mAnimationDrawable = animationDrawable;
    }

    @Override
    public void setPercent(float percent, boolean invalidate) {
        setPercent(percent);
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mContainerView.scrollTo(0, offset);
    }

    @Override
    public void start() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.start();
        }
    }

    @Override
    public boolean isRunning() {
        return mAnimationDrawable == null ? false : mAnimationDrawable.isRunning();
    }

    @Override
    public void stop() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
    }

    @Override
    public Drawable obtainRefreshDrawable() {
        return mAnimationDrawable;
    }
}
