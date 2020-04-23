package com.bumptech.glide.load.data;

import com.bumptech.glide.Priority;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArrayFetcher implements DataFetcher<InputStream> {
    private final byte[] bytes;
    private final String id;

    public ByteArrayFetcher(byte[] bytes2, String id2) {
        this.bytes = bytes2;
        this.id = id2;
    }

    public InputStream loadData(Priority priority) {
        return new ByteArrayInputStream(this.bytes);
    }

    public void cleanup() {
    }

    public String getId() {
        return this.id;
    }

    public void cancel() {
    }
}
