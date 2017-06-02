package com.yalantis.phoenix.refresh_view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.yalantis.phoenix.PullToRefreshView;

/**
 * @author chenyongkang
 * @Date 2017/6/1 14:19
 */
public class MoreRefreshView implements SuperRefreshView {// implements Animatable {

    private static final String TAG = PullToRefreshView.TAG;

    private PullToRefreshView mParent;

    private Context mContext;

    private AnimationDrawable mAnnimationDrawable;

    private float mPercent = 0.0f;

    private ImageView mContainerView;

    private int mContainerTop;

    public MoreRefreshView(Context context, final PullToRefreshView parent, ImageView containerView) {
        this.mParent = parent;
        this.mContext = context;
        this.mContainerView = containerView;
        this.mContainerTop = mContainerView.getTop();
//        parent.post(new Runnable() {
//            @Override
//            public void run() {
//                initAnimationDrawable(parent.getHeight());
//            }
//        });
    }

    public void setAnimationDrawable(AnimationDrawable drawable) {
        this.mAnnimationDrawable = drawable;
    }

    @Override
    public void start() {
        if (mAnnimationDrawable != null) {
            mAnnimationDrawable.start();
        }
    }

    @Override
    public boolean isRunning() {
        return mAnnimationDrawable == null ? false : mAnnimationDrawable.isRunning();
    }

    @Override
    public void stop() {
        if (mAnnimationDrawable != null) {
            mAnnimationDrawable.stop();
        }
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        Log.d(TAG, "more refresh view offsetTopAndBottom " + offset);
        mContainerView.scrollTo(0, offset);
    }

    @Override
    public Drawable getRefreshDrawable() {
        return mAnnimationDrawable;
    }

    @Override
    public void setPercent(float percent, boolean invalidate) {
        setPercent(percent);
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }

//    void initAnimationDrawable(int height){
//        mContainerView.scrollTo(0,30);
//    }
}
