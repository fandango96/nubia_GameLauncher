package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import java.io.File;

public class Volley {
    private static final String DEFAULT_CACHE_DIR = "volley";

    public static RequestQueue newRequestQueue(Context context, BaseHttpStack stack) {
        BasicNetwork network;
        if (stack != null) {
            network = new BasicNetwork(stack);
        } else if (VERSION.SDK_INT >= 9) {
            network = new BasicNetwork((BaseHttpStack) new HurlStack());
        } else {
            String userAgent = "volley/0";
            try {
                String packageName = context.getPackageName();
                userAgent = packageName + "/" + context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
            } catch (NameNotFoundException e) {
            }
            network = new BasicNetwork((HttpStack) new HttpClientStack(AndroidHttpClient.newInstance(userAgent)));
        }
        return newRequestQueue(context, (Network) network);
    }

    @Deprecated
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        if (stack == null) {
            return newRequestQueue(context, (BaseHttpStack) null);
        }
        return newRequestQueue(context, (Network) new BasicNetwork(stack));
    }

    private static RequestQueue newRequestQueue(Context context, Network network) {
        RequestQueue queue = new RequestQueue(new DiskBasedCache(new File(context.getCacheDir(), DEFAULT_CACHE_DIR)), network);
        queue.start();
        return queue;
    }

    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, (BaseHttpStack) null);
    }
}
