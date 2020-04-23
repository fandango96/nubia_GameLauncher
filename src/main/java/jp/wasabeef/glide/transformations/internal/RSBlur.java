package jp.wasabeef.glide.transformations.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Allocation.MipmapControl;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.RenderScript.RSMessageHandler;
import android.renderscript.ScriptIntrinsicBlur;

public class RSBlur {
    @TargetApi(18)
    public static Bitmap blur(Context context, Bitmap bitmap, int radius) throws RSRuntimeException {
        RenderScript rs = null;
        try {
            rs = RenderScript.create(context);
            rs.setMessageHandler(new RSMessageHandler());
            Allocation input = Allocation.createFromBitmap(rs, bitmap, MipmapControl.MIPMAP_NONE, 1);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blur.setInput(input);
            blur.setRadius((float) radius);
            blur.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        } finally {
            if (rs != null) {
                rs.destroy();
            }
        }
    }
}
