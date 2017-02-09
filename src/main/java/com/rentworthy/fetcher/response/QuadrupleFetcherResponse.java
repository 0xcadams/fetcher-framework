package com.rentworthy.fetcher.response;

import com.rentworthy.fetcher.response.source.QuadrupleSource;

public class QuadrupleFetcherResponse<T> implements FetcherResponse<T> {

    private final QuadrupleSource source;
    private final T value;

    /**
     * @param source
     * @param value
     */
    public QuadrupleFetcherResponse(final QuadrupleSource source, final T value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public QuadrupleSource source() {
        return this.source;
    }

    @Override
    public T value() {
        return this.value;
    }

}
