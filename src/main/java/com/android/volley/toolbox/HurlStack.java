package com.android.volley.toolbox;

import android.support.annotation.VisibleForTesting;
import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpClientStack.HttpPatch;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class HurlStack extends BaseHttpStack {
    private static final int HTTP_CONTINUE = 100;
    private final SSLSocketFactory mSslSocketFactory;
    private final UrlRewriter mUrlRewriter;

    static class UrlConnectionInputStream extends FilterInputStream {
        private final HttpURLConnection mConnection;

        UrlConnectionInputStream(HttpURLConnection connection) {
            super(HurlStack.inputStreamFromConnection(connection));
            this.mConnection = connection;
        }

        public void close() throws IOException {
            super.close();
            this.mConnection.disconnect();
        }
    }

    public interface UrlRewriter {
        String rewriteUrl(String str);
    }

    public HurlStack() {
        this(null);
    }

    public HurlStack(UrlRewriter urlRewriter) {
        this(urlRewriter, null);
    }

    public HurlStack(UrlRewriter urlRewriter, SSLSocketFactory sslSocketFactory) {
        this.mUrlRewriter = urlRewriter;
        this.mSslSocketFactory = sslSocketFactory;
    }

    /* JADX INFO: finally extract failed */
    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        HttpResponse httpResponse;
        String url = request.getUrl();
        HashMap<String, String> map = new HashMap<>();
        map.putAll(additionalHeaders);
        map.putAll(request.getHeaders());
        if (this.mUrlRewriter != null) {
            String rewritten = this.mUrlRewriter.rewriteUrl(url);
            if (rewritten == null) {
                throw new IOException("URL blocked by rewriter: " + url);
            }
            url = rewritten;
        }
        HttpURLConnection connection = openConnection(new URL(url), request);
        boolean keepConnectionOpen = false;
        try {
            for (String headerName : map.keySet()) {
                connection.setRequestProperty(headerName, (String) map.get(headerName));
            }
            setConnectionParametersForRequest(connection, request);
            int responseCode = connection.getResponseCode();
            if (responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            }
            if (!hasResponseBody(request.getMethod(), responseCode)) {
                httpResponse = new HttpResponse(responseCode, convertHeaders(connection.getHeaderFields()));
                if (0 == 0) {
                    connection.disconnect();
                }
            } else {
                keepConnectionOpen = true;
                httpResponse = new HttpResponse(responseCode, convertHeaders(connection.getHeaderFields()), connection.getContentLength(), new UrlConnectionInputStream(connection));
                if (1 == 0) {
                    connection.disconnect();
                }
            }
            return httpResponse;
        } catch (Throwable th) {
            if (!keepConnectionOpen) {
                connection.disconnect();
            }
            throw th;
        }
    }

    @VisibleForTesting
    static List<Header> convertHeaders(Map<String, List<String>> responseHeaders) {
        List<Header> headerList = new ArrayList<>(responseHeaders.size());
        for (Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            if (entry.getKey() != null) {
                for (String value : (List) entry.getValue()) {
                    headerList.add(new Header((String) entry.getKey(), value));
                }
            }
        }
        return headerList;
    }

    private static boolean hasResponseBody(int requestMethod, int responseCode) {
        return (requestMethod == 4 || (100 <= responseCode && responseCode < 200) || responseCode == 204 || responseCode == 304) ? false : true;
    }

    /* access modifiers changed from: private */
    public static InputStream inputStreamFromConnection(HttpURLConnection connection) {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            return connection.getErrorStream();
        }
    }

    /* access modifiers changed from: protected */
    public HttpURLConnection createConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(HttpURLConnection.getFollowRedirects());
        return connection;
    }

    private HttpURLConnection openConnection(URL url, Request<?> request) throws IOException {
        HttpURLConnection connection = createConnection(url);
        int timeoutMs = request.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);
        connection.setUseCaches(false);
        connection.setDoInput(true);
        if ("https".equals(url.getProtocol()) && this.mSslSocketFactory != null) {
            ((HttpsURLConnection) connection).setSSLSocketFactory(this.mSslSocketFactory);
        }
        return connection;
    }

    static void setConnectionParametersForRequest(HttpURLConnection connection, Request<?> request) throws IOException, AuthFailureError {
        switch (request.getMethod()) {
            case -1:
                byte[] postBody = request.getPostBody();
                if (postBody != null) {
                    connection.setRequestMethod("POST");
                    addBody(connection, request, postBody);
                    return;
                }
                return;
            case 0:
                connection.setRequestMethod("GET");
                return;
            case 1:
                connection.setRequestMethod("POST");
                addBodyIfExists(connection, request);
                return;
            case 2:
                connection.setRequestMethod("PUT");
                addBodyIfExists(connection, request);
                return;
            case 3:
                connection.setRequestMethod("DELETE");
                return;
            case 4:
                connection.setRequestMethod("HEAD");
                return;
            case 5:
                connection.setRequestMethod("OPTIONS");
                return;
            case 6:
                connection.setRequestMethod("TRACE");
                return;
            case 7:
                connection.setRequestMethod(HttpPatch.METHOD_NAME);
                addBodyIfExists(connection, request);
                return;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static void addBodyIfExists(HttpURLConnection connection, Request<?> request) throws IOException, AuthFailureError {
        byte[] body = request.getBody();
        if (body != null) {
            addBody(connection, request, body);
        }
    }

    private static void addBody(HttpURLConnection connection, Request<?> request, byte[] body) throws IOException {
        connection.setDoOutput(true);
        if (!connection.getRequestProperties().containsKey("Content-Type")) {
            connection.setRequestProperty("Content-Type", request.getBodyContentType());
        }
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.write(body);
        out.close();
    }
}
