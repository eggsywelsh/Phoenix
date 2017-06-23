package com.yalantis.phoenix.refresh_view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

/**
 * more refreshing view
 *
 * @author chenyongkang
 * @Date 2017/6/1 14:19
 */
public class MoreRefreshView extends BaseAnimationRefreshView {

    public MoreRefreshView(Context context, ImageView containerView,AnimationDrawable drawable) {
        super(context,containerView,drawable);
    }
}
