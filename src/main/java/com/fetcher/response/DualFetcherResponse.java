package com.fetcher.response;

import com.fetcher.response.source.DualSource;

public class DualFetcherResponse<T> implements FetcherResponse<T> {

    private final DualSource source;
    private final T value;

    /**
     * @param source
     * @param value
     */
    public DualFetcherResponse(final DualSource source, final T value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public DualSource source() {
        return this.source;
    }

    @Override
    public T value() {
        return this.value;
    }

}
