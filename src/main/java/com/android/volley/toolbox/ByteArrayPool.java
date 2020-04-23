package com.android.volley.toolbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ByteArrayPool {
    protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
        public int compare(byte[] lhs, byte[] rhs) {
            return lhs.length - rhs.length;
        }
    };
    private final List<byte[]> mBuffersByLastUse = new ArrayList();
    private final List<byte[]> mBuffersBySize = new ArrayList(64);
    private int mCurrentSize = 0;
    private final int mSizeLimit;

    public ByteArrayPool(int sizeLimit) {
        this.mSizeLimit = sizeLimit;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0 = new byte[r5];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized byte[] getBuf(int r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            r1 = 0
        L_0x0002:
            java.util.List<byte[]> r2 = r4.mBuffersBySize     // Catch:{ all -> 0x002d }
            int r2 = r2.size()     // Catch:{ all -> 0x002d }
            if (r1 >= r2) goto L_0x002a
            java.util.List<byte[]> r2 = r4.mBuffersBySize     // Catch:{ all -> 0x002d }
            java.lang.Object r0 = r2.get(r1)     // Catch:{ all -> 0x002d }
            byte[] r0 = (byte[]) r0     // Catch:{ all -> 0x002d }
            int r2 = r0.length     // Catch:{ all -> 0x002d }
            if (r2 < r5) goto L_0x0027
            int r2 = r4.mCurrentSize     // Catch:{ all -> 0x002d }
            int r3 = r0.length     // Catch:{ all -> 0x002d }
            int r2 = r2 - r3
            r4.mCurrentSize = r2     // Catch:{ all -> 0x002d }
            java.util.List<byte[]> r2 = r4.mBuffersBySize     // Catch:{ all -> 0x002d }
            r2.remove(r1)     // Catch:{ all -> 0x002d }
            java.util.List<byte[]> r2 = r4.mBuffersByLastUse     // Catch:{ all -> 0x002d }
            r2.remove(r0)     // Catch:{ all -> 0x002d }
        L_0x0025:
            monitor-exit(r4)
            return r0
        L_0x0027:
            int r1 = r1 + 1
            goto L_0x0002
        L_0x002a:
            byte[] r0 = new byte[r5]     // Catch:{ all -> 0x002d }
            goto L_0x0025
        L_0x002d:
            r2 = move-exception
            monitor-exit(r4)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.toolbox.ByteArrayPool.getBuf(int):byte[]");
    }

    public synchronized void returnBuf(byte[] buf) {
        if (buf != null) {
            if (buf.length <= this.mSizeLimit) {
                this.mBuffersByLastUse.add(buf);
                int pos = Collections.binarySearch(this.mBuffersBySize, buf, BUF_COMPARATOR);
                if (pos < 0) {
                    pos = (-pos) - 1;
                }
                this.mBuffersBySize.add(pos, buf);
                this.mCurrentSize += buf.length;
                trim();
            }
        }
    }

    private synchronized void trim() {
        while (this.mCurrentSize > this.mSizeLimit) {
            byte[] buf = (byte[]) this.mBuffersByLastUse.remove(0);
            this.mBuffersBySize.remove(buf);
            this.mCurrentSize -= buf.length;
        }
    }
}
