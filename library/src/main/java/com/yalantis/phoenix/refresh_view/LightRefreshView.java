package com.yalantis.phoenix.refresh_view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.yalantis.phoenix.PullToRefreshView;
import com.yalantis.phoenix.R;

/**
 * @author chenyongkang
 * @Date 2017/6/5 17:34
 */
public class LightRefreshView extends BaseDrawableRefreshView implements Animatable {

    private static final int ANIMATION_DURATION = 2000;

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

    private static final int MIN_ANIMATION_PLAY_INDEX = 0;

    private static final int MID_ANIMATION_PLAY_INDEX = 12;

    private static final int MAX_ANIMATION_PLAY_INDEX = 24;

    private int mTop;
    private int mScreenWidth;

    private int mLightHeight = 0;
    private int mLightWidth = 0;

    private PullToRefreshView mParent;

    private ImageView mContainerView;

    private Context mContext;

    private float mPercent = 0.0f;

    private int mAnimationPlayIndex = 0;

    private boolean isRefreshing = false;

    private ValueAnimator mAnimation;

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;

    private AnimationDrawable mSourceAnimationDrawable;

    private Paint mPaint;

    private Bitmap mLine;

    public LightRefreshView(Context context, final PullToRefreshView parent, ImageView containerView) {

        super(context, parent);

        this.mContext = context;

        this.mParent = parent;

        this.mContainerView = containerView;

        this.mPaint = new Paint();

        setupDrawable();

        setupAnimations();

        parent.post(new Runnable() {
            @Override
            public void run() {
                initiateDimens(parent.getWidth());
            }
        });
    }

    public void initiateDimens(int viewWidth) {
        if (viewWidth <= 0 || viewWidth == mScreenWidth) return;

        mScreenWidth = viewWidth;

        mContainerView.setBackground(mContext.getResources().getDrawable(R.drawable.moon_header_bg));

        mContainerView.setScaleType(ImageView.ScaleType.FIT_XY);

        mLightWidth = mContext.getResources().getDimensionPixelSize(R.dimen.refresh_view_light_width);
        mLightHeight = mContext.getResources().getDimensionPixelSize(R.dimen.refresh_view_light_height);

        createBitmaps();
    }

    private void createBitmaps() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        mLine = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.moon_common_loading_line, options);
        mLine = Bitmap.createScaledBitmap(mLine, mLine.getWidth(), mParent.getMeasuredHeight(), true);
    }

    private void setupDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSourceAnimationDrawable = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.moon_common_state_loading_ani, null);
        } else {
            mSourceAnimationDrawable = (AnimationDrawable) mContext.getResources().getDrawable(R.drawable.moon_common_state_loading_ani);
        }

        mLightHeight = mSourceAnimationDrawable.getIntrinsicHeight();
        mLightWidth = mSourceAnimationDrawable.getIntrinsicWidth();
    }

    /**
     * init setup animations
     */
    private void setupAnimations() {
        mAnimation = ValueAnimator.ofInt(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(ValueAnimator.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);

        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int index = (int) animation.getAnimatedValue();
                Log.d(PullToRefreshView.TAG,"index "+index);
                mAnimationPlayIndex = index;
                invalidateSelf();
            }
        };
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mScreenWidth <= 0) return;

        final int saveCount = canvas.save();

        drawLine(canvas);
        drawLight(canvas);

        canvas.restoreToCount(saveCount);
    }

    private void drawLine(Canvas canvas) {
        if (mParent.getTargetOffsetTop() > mLightHeight) {
            Log.d(PullToRefreshView.TAG,
                    "left " + (mScreenWidth - mLine.getWidth() / 3) / 2 +
                            " , top " + 0 +
                            " , right " + ((mScreenWidth - mLine.getWidth() / 3) / 2 + mLine.getWidth() / 3) +
                            " , bottom " + (mParent.getTargetOffsetTop() - mLightHeight));
            // need draw line
//            Rect srcRect = new Rect((mScreenWidth - mLine.getWidth() / 3) / 2, 0,
//                    (mScreenWidth - mLine.getWidth() / 3) / 2 + mLine.getWidth(),
//                    mParent.getTargetOffsetTop() - mLightHeight);
            RectF dstRect = new RectF((mScreenWidth - mLine.getWidth()) / 2, 0,
                    (mScreenWidth - mLine.getWidth()) / 2 + mLine.getWidth(),
                    mParent.getTargetOffsetTop() - mLightHeight);
            canvas.drawBitmap(mLine, null, dstRect, mPaint);
//            canvas.drawBitmap(mLine, (mScreenWidth - mLine.getWidth() / 3) / 2, 0, mPaint);
        }
    }

    private void drawLight(Canvas canvas) {
        if (mParent.getTargetOffsetTop() > mParent.getTopTotalDragDistance()) {
            Log.d(PullToRefreshView.TAG,"[drawLight] more. mParent.getTargetOffsetTop()="+mParent.getTargetOffsetTop()+" , mLightHeight="+mLightHeight);
            Drawable d = mSourceAnimationDrawable.getFrame(MAX_ANIMATION_PLAY_INDEX);
            d.setBounds((mScreenWidth - d.getIntrinsicWidth()) / 2,
                    mParent.getTargetOffsetTop() - mLightHeight,
                    (mScreenWidth - d.getIntrinsicWidth()) / 2 + d.getIntrinsicWidth(),
                    mParent.getTargetOffsetTop()
            );
            d.draw(canvas);
        } else if(mParent.getTargetOffsetTop() == mParent.getTopTotalDragDistance()){
            Log.d(PullToRefreshView.TAG,"[drawLight] less. mParent.getTargetOffsetTop()="+mParent.getTargetOffsetTop()+" , mLightHeight="+mLightHeight);
            Drawable d = mSourceAnimationDrawable.getFrame(mAnimationPlayIndex);
            d.setBounds((mScreenWidth - d.getIntrinsicWidth()) / 2,
                    mParent.getTargetOffsetTop() - mLightHeight,
                    (mScreenWidth - d.getIntrinsicWidth()) / 2 + d.getIntrinsicWidth(),
                    mParent.getTargetOffsetTop()
            );
            d.draw(canvas);
//            mSourceAnimationDrawable.setBounds((mScreenWidth - mSourceAnimationDrawable.getIntrinsicWidth()) / 2,
//                    mParent.getTargetOffsetTop() - mLightHeight,
//                    (mScreenWidth - mSourceAnimationDrawable.getIntrinsicWidth()) / 2 + mSourceAnimationDrawable.getIntrinsicWidth(),
//                    mParent.getTargetOffsetTop()
//            );
//            mSourceAnimationDrawable.draw(canvas);
        } else {
            if(mAnimationPlayIndex <= MIN_ANIMATION_PLAY_INDEX){
                mAnimationPlayIndex = MAX_ANIMATION_PLAY_INDEX;
            }
            Log.d(PullToRefreshView.TAG,"[drawLight] less. mParent.getTargetOffsetTop()="+mParent.getTargetOffsetTop()+" , mLightHeight="+mLightHeight);
            Drawable d = mSourceAnimationDrawable.getFrame(mAnimationPlayIndex);
            d.setBounds((mScreenWidth - d.getIntrinsicWidth()) / 2,
                    mParent.getTargetOffsetTop() - mLightHeight,
                    (mScreenWidth - d.getIntrinsicWidth()) / 2 + d.getIntrinsicWidth(),
                    mParent.getTargetOffsetTop()
            );
            d.draw(canvas);
        }
    }

    @Override
    public void start() {
        mAnimation.addUpdateListener(mAnimatorUpdateListener);
        isRefreshing = true;
        mAnimation.start();
    }

    @Override
    public void stop() {
        mAnimation.removeUpdateListener(mAnimatorUpdateListener);
        mAnimation.end();
        isRefreshing = false;
        resetOriginals();
    }

    public void resetOriginals() {
        setPercent(0);
        setDrawableIndex(0);
    }

    private void setDrawableIndex(float percent) {
        if (percent < 0) {
            mAnimationPlayIndex = MID_ANIMATION_PLAY_INDEX;
        } else if (percent >= 1) {
            mAnimationPlayIndex = MIN_ANIMATION_PLAY_INDEX;
        } else {
            mAnimationPlayIndex = (int) ((percent * MAX_ANIMATION_PLAY_INDEX + MID_ANIMATION_PLAY_INDEX) % MAX_ANIMATION_PLAY_INDEX);
        }
        invalidateSelf();
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Drawable obtainRefreshDrawable() {
        return this;
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        invalidateSelf();
    }

    @Override
    public void setPercent(float percent, boolean invalidate) {
        Log.d(PullToRefreshView.TAG, "percent " + percent);
        setPercent(percent);
        if (invalidate) setDrawableIndex(percent);
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }

    @Override
    public int getOpacity() {
        return super.getOpacity();
    }

    @Override
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
    }
}
