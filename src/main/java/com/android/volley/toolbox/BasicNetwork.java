package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.Cache.Entry;
import com.android.volley.Header;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;
    private static final int DEFAULT_POOL_SIZE = 4096;
    private static final int SLOW_REQUEST_THRESHOLD_MS = 3000;
    private final BaseHttpStack mBaseHttpStack;
    @Deprecated
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    @Deprecated
    public BasicNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(4096));
    }

    @Deprecated
    public BasicNetwork(HttpStack httpStack, ByteArrayPool pool) {
        this.mHttpStack = httpStack;
        this.mBaseHttpStack = new AdaptedHttpStack(httpStack);
        this.mPool = pool;
    }

    public BasicNetwork(BaseHttpStack httpStack) {
        this(httpStack, new ByteArrayPool(4096));
    }

    public BasicNetwork(BaseHttpStack httpStack, ByteArrayPool pool) {
        this.mBaseHttpStack = httpStack;
        this.mHttpStack = httpStack;
        this.mPool = pool;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x008e, code lost:
        throw new java.io.IOException();
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0116 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.volley.NetworkResponse performRequest(com.android.volley.Request<?> r26) throws com.android.volley.VolleyError {
        /*
            r25 = this;
            long r22 = android.os.SystemClock.elapsedRealtime()
        L_0x0004:
            r20 = 0
            r24 = 0
            java.util.List r8 = java.util.Collections.emptyList()
            com.android.volley.Cache$Entry r3 = r26.getCacheEntry()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r0 = r25
            java.util.Map r17 = r0.getCacheHeaders(r3)     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r0 = r25
            com.android.volley.toolbox.BaseHttpStack r3 = r0.mBaseHttpStack     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r0 = r26
            r1 = r17
            com.android.volley.toolbox.HttpResponse r20 = r3.executeRequest(r0, r1)     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            int r14 = r20.getStatusCode()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            java.util.List r8 = r20.getHeaders()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r3 = 304(0x130, float:4.26E-43)
            if (r14 != r3) goto L_0x0062
            com.android.volley.Cache$Entry r19 = r26.getCacheEntry()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            if (r19 != 0) goto L_0x0046
            com.android.volley.NetworkResponse r2 = new com.android.volley.NetworkResponse     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r3 = 304(0x130, float:4.26E-43)
            r4 = 0
            r5 = 1
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            long r6 = r6 - r22
            r2.<init>(r3, r4, r5, r6, r8)     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r13 = r24
        L_0x0045:
            return r2
        L_0x0046:
            r0 = r19
            java.util.List r16 = combineHeaders(r8, r0)     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            com.android.volley.NetworkResponse r10 = new com.android.volley.NetworkResponse     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r11 = 304(0x130, float:4.26E-43)
            r0 = r19
            byte[] r12 = r0.data     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r13 = 1
            long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            long r14 = r4 - r22
            r10.<init>(r11, r12, r13, r14, r16)     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r13 = r24
            r2 = r10
            goto L_0x0045
        L_0x0062:
            java.io.InputStream r21 = r20.getContent()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            if (r21 == 0) goto L_0x009e
            int r3 = r20.getContentLength()     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            r0 = r25
            r1 = r21
            byte[] r13 = r0.inputStreamToBytes(r1, r3)     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
        L_0x0074:
            long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            long r10 = r4 - r22
            r9 = r25
            r12 = r26
            r9.logSlowRequests(r10, r12, r13, r14)     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            r3 = 200(0xc8, float:2.8E-43)
            if (r14 < r3) goto L_0x0089
            r3 = 299(0x12b, float:4.19E-43)
            if (r14 <= r3) goto L_0x00a2
        L_0x0089:
            java.io.IOException r3 = new java.io.IOException     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            r3.<init>()     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            throw r3     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
        L_0x008f:
            r18 = move-exception
        L_0x0090:
            java.lang.String r3 = "socket"
            com.android.volley.TimeoutError r4 = new com.android.volley.TimeoutError
            r4.<init>()
            r0 = r26
            attemptRetryOnException(r3, r0, r4)
            goto L_0x0004
        L_0x009e:
            r3 = 0
            byte[] r13 = new byte[r3]     // Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x0165, IOException -> 0x00d1 }
            goto L_0x0074
        L_0x00a2:
            com.android.volley.NetworkResponse r2 = new com.android.volley.NetworkResponse     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            r5 = 0
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            long r6 = r6 - r22
            r3 = r14
            r4 = r13
            r2.<init>(r3, r4, r5, r6, r8)     // Catch:{ SocketTimeoutException -> 0x008f, MalformedURLException -> 0x00b1, IOException -> 0x0162 }
            goto L_0x0045
        L_0x00b1:
            r18 = move-exception
        L_0x00b2:
            java.lang.RuntimeException r3 = new java.lang.RuntimeException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Bad URL "
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = r26.getUrl()
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r4 = r4.toString()
            r0 = r18
            r3.<init>(r4, r0)
            throw r3
        L_0x00d1:
            r18 = move-exception
            r13 = r24
        L_0x00d4:
            if (r20 == 0) goto L_0x0116
            int r14 = r20.getStatusCode()
            java.lang.String r3 = "Unexpected response code %d for %s"
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            java.lang.Integer r6 = java.lang.Integer.valueOf(r14)
            r4[r5] = r6
            r5 = 1
            java.lang.String r6 = r26.getUrl()
            r4[r5] = r6
            com.android.volley.VolleyLog.e(r3, r4)
            if (r13 == 0) goto L_0x0154
            com.android.volley.NetworkResponse r2 = new com.android.volley.NetworkResponse
            r5 = 0
            long r6 = android.os.SystemClock.elapsedRealtime()
            long r6 = r6 - r22
            r3 = r14
            r4 = r13
            r2.<init>(r3, r4, r5, r6, r8)
            r3 = 401(0x191, float:5.62E-43)
            if (r14 == r3) goto L_0x0108
            r3 = 403(0x193, float:5.65E-43)
            if (r14 != r3) goto L_0x011e
        L_0x0108:
            java.lang.String r3 = "auth"
            com.android.volley.AuthFailureError r4 = new com.android.volley.AuthFailureError
            r4.<init>(r2)
            r0 = r26
            attemptRetryOnException(r3, r0, r4)
            goto L_0x0004
        L_0x0116:
            com.android.volley.NoConnectionError r3 = new com.android.volley.NoConnectionError
            r0 = r18
            r3.<init>(r0)
            throw r3
        L_0x011e:
            r3 = 400(0x190, float:5.6E-43)
            if (r14 < r3) goto L_0x012c
            r3 = 499(0x1f3, float:6.99E-43)
            if (r14 > r3) goto L_0x012c
            com.android.volley.ClientError r3 = new com.android.volley.ClientError
            r3.<init>(r2)
            throw r3
        L_0x012c:
            r3 = 500(0x1f4, float:7.0E-43)
            if (r14 < r3) goto L_0x014e
            r3 = 599(0x257, float:8.4E-43)
            if (r14 > r3) goto L_0x014e
            boolean r3 = r26.shouldRetryServerErrors()
            if (r3 == 0) goto L_0x0148
            java.lang.String r3 = "server"
            com.android.volley.ServerError r4 = new com.android.volley.ServerError
            r4.<init>(r2)
            r0 = r26
            attemptRetryOnException(r3, r0, r4)
            goto L_0x0004
        L_0x0148:
            com.android.volley.ServerError r3 = new com.android.volley.ServerError
            r3.<init>(r2)
            throw r3
        L_0x014e:
            com.android.volley.ServerError r3 = new com.android.volley.ServerError
            r3.<init>(r2)
            throw r3
        L_0x0154:
            java.lang.String r3 = "network"
            com.android.volley.NetworkError r4 = new com.android.volley.NetworkError
            r4.<init>()
            r0 = r26
            attemptRetryOnException(r3, r0, r4)
            goto L_0x0004
        L_0x0162:
            r18 = move-exception
            goto L_0x00d4
        L_0x0165:
            r18 = move-exception
            r13 = r24
            goto L_0x00b2
        L_0x016a:
            r18 = move-exception
            r13 = r24
            goto L_0x0090
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.toolbox.BasicNetwork.performRequest(com.android.volley.Request):com.android.volley.NetworkResponse");
    }

    private void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents, int statusCode) {
        if (DEBUG || requestLifetime > 3000) {
            String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
            Object[] objArr = new Object[5];
            objArr[0] = request;
            objArr[1] = Long.valueOf(requestLifetime);
            objArr[2] = responseContents != null ? Integer.valueOf(responseContents.length) : "null";
            objArr[3] = Integer.valueOf(statusCode);
            objArr[4] = Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount());
            VolleyLog.d(str, objArr);
        }
    }

    private static void attemptRetryOnException(String logPrefix, Request<?> request, VolleyError exception) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();
        try {
            retryPolicy.retry(exception);
            request.addMarker(String.format("%s-retry [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
        } catch (VolleyError e) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", new Object[]{logPrefix, Integer.valueOf(oldTimeout)}));
            throw e;
        }
    }

    private Map<String, String> getCacheHeaders(Entry entry) {
        if (entry == null) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new HashMap<>();
        if (entry.etag != null) {
            headers.put("If-None-Match", entry.etag);
        }
        if (entry.lastModified <= 0) {
            return headers;
        }
        headers.put("If-Modified-Since", HttpHeaderParser.formatEpochAsRfc1123(entry.lastModified));
        return headers;
    }

    /* access modifiers changed from: protected */
    public void logError(String what, String url, long start) {
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, Long.valueOf(SystemClock.elapsedRealtime() - start), url);
    }

    private byte[] inputStreamToBytes(InputStream in, int contentLength) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(this.mPool, contentLength);
        if (in == null) {
            try {
                throw new ServerError();
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        VolleyLog.v("Error occurred when closing InputStream", new Object[0]);
                    }
                }
                this.mPool.returnBuf(null);
                bytes.close();
                throw th;
            }
        } else {
            byte[] buffer = this.mPool.getBuf(1024);
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                bytes.write(buffer, 0, count);
            }
            byte[] byteArray = bytes.toByteArray();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e2) {
                    VolleyLog.v("Error occurred when closing InputStream", new Object[0]);
                }
            }
            this.mPool.returnBuf(buffer);
            bytes.close();
            return byteArray;
        }
    }

    @Deprecated
    protected static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }

    private static List<Header> combineHeaders(List<Header> responseHeaders, Entry entry) {
        Set<String> headerNamesFromNetworkResponse = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (!responseHeaders.isEmpty()) {
            for (Header header : responseHeaders) {
                headerNamesFromNetworkResponse.add(header.getName());
            }
        }
        List<Header> combinedHeaders = new ArrayList<>(responseHeaders);
        if (entry.allResponseHeaders != null) {
            if (!entry.allResponseHeaders.isEmpty()) {
                for (Header header2 : entry.allResponseHeaders) {
                    if (!headerNamesFromNetworkResponse.contains(header2.getName())) {
                        combinedHeaders.add(header2);
                    }
                }
            }
        } else if (!entry.responseHeaders.isEmpty()) {
            for (Map.Entry<String, String> header3 : entry.responseHeaders.entrySet()) {
                if (!headerNamesFromNetworkResponse.contains(header3.getKey())) {
                    combinedHeaders.add(new Header((String) header3.getKey(), (String) header3.getValue()));
                }
            }
        }
        return combinedHeaders;
    }
}
