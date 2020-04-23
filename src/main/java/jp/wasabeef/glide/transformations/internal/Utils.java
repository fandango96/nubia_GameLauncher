package jp.wasabeef.glide.transformations.internal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;

public final class Utils {
    private Utils() {
    }

    public static Drawable getMaskDrawable(Context context, int maskId) {
        Drawable drawable;
        if (VERSION.SDK_INT >= 21) {
            drawable = context.getDrawable(maskId);
        } else {
            drawable = context.getResources().getDrawable(maskId);
        }
        if (drawable != null) {
            return drawable;
        }
        throw new IllegalArgumentException("maskId is invalid");
    }
}
