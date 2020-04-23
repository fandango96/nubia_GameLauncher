package com.android.volley.toolbox;

import android.support.annotation.GuardedBy;
import android.support.annotation.Nullable;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import java.io.UnsupportedEncodingException;

public class StringRequest extends Request<String> {
    @Nullable
    @GuardedBy("mLock")
    private Listener<String> mListener;
    private final Object mLock;

    public StringRequest(int method, String url, Listener<String> listener, @Nullable ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mLock = new Object();
        this.mListener = listener;
    }

    public StringRequest(String url, Listener<String> listener, @Nullable ErrorListener errorListener) {
        this(0, url, listener, errorListener);
    }

    public void cancel() {
        super.cancel();
        synchronized (this.mLock) {
            this.mListener = null;
        }
    }

    /* access modifiers changed from: protected */
    public void deliverResponse(String response) {
        Listener<String> listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    /* access modifiers changed from: protected */
    public Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
