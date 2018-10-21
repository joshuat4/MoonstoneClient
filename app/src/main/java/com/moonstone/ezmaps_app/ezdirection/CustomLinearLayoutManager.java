package com.moonstone.ezmaps_app.ezdirection;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;


/* Custom LinearL Layout to handle disabling and enabling scrolling */
public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnabled = true;

    public CustomLinearLayoutManager(Context context, int layoutType, boolean reverseLayout) {
        super(context, layoutType, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollHorizontally() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollHorizontally();
    }
}
