package com.android.volley.toolbox;

import android.support.annotation.Nullable;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import java.io.UnsupportedEncodingException;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonArrayRequest extends JsonRequest<JSONArray> {
    public JsonArrayRequest(String url, Listener<JSONArray> listener, @Nullable ErrorListener errorListener) {
        super(0, url, null, listener, errorListener);
    }

    public JsonArrayRequest(int method, String url, @Nullable JSONArray jsonRequest, Listener<JSONArray> listener, @Nullable ErrorListener errorListener) {
        String jSONArray;
        if (jsonRequest == null) {
            jSONArray = null;
        } else {
            jSONArray = jsonRequest.toString();
        }
        super(method, url, jSONArray, listener, errorListener);
    }

    /* access modifiers changed from: protected */
    public Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            return Response.success(new JSONArray(new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"))), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError((Throwable) e));
        } catch (JSONException je) {
            return Response.error(new ParseError((Throwable) je));
        }
    }
}
