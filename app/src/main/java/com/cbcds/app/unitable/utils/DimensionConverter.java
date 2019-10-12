package com.cbcds.app.unitable.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DimensionConverter {

    public static int dpToPx(Context context, float dp) {
        Resources res = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                res.getDisplayMetrics());
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
