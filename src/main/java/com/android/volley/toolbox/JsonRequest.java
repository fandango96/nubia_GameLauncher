package com.android.volley.toolbox;

import android.support.annotation.GuardedBy;
import android.support.annotation.Nullable;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import java.io.UnsupportedEncodingException;

public abstract class JsonRequest<T> extends Request<T> {
    protected static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", new Object[]{PROTOCOL_CHARSET});
    @Nullable
    @GuardedBy("mLock")
    private Listener<T> mListener;
    private final Object mLock;
    @Nullable
    private final String mRequestBody;

    /* access modifiers changed from: protected */
    public abstract Response<T> parseNetworkResponse(NetworkResponse networkResponse);

    @Deprecated
    public JsonRequest(String url, String requestBody, Listener<T> listener, ErrorListener errorListener) {
        this(-1, url, requestBody, listener, errorListener);
    }

    public JsonRequest(int method, String url, @Nullable String requestBody, Listener<T> listener, @Nullable ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mLock = new Object();
        this.mListener = listener;
        this.mRequestBody = requestBody;
    }

    public void cancel() {
        super.cancel();
        synchronized (this.mLock) {
            this.mListener = null;
        }
    }

    /* access modifiers changed from: protected */
    public void deliverResponse(T response) {
        Listener<T> listener;
        synchronized (this.mLock) {
            listener = this.mListener;
        }
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    @Deprecated
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    @Deprecated
    public byte[] getPostBody() {
        return getBody();
    }

    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    public byte[] getBody() {
        try {
            if (this.mRequestBody == null) {
                return null;
            }
            return this.mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException e) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", this.mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
