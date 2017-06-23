package com.yalantis.phoenix.refresh_view;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

/**
 * super refreshing view
 *
 * @author chenyongkang
 * @Date 2017/6/1 16:20
 */
public interface SuperRefreshView extends Animatable {

    void setPercent(float percent, boolean invalidate);

    void offsetTopAndBottom(int offset);

    Drawable obtainRefreshDrawable();

}
