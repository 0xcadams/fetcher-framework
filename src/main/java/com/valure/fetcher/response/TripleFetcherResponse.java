package com.valure.fetcher.response;

import com.valure.fetcher.response.source.TripleSource;

public class TripleFetcherResponse<T> implements FetcherResponse<T> {

    private final TripleSource source;
    private final T value;

    /**
     * @param source
     * @param value
     */
    public TripleFetcherResponse(final TripleSource source, final T value) {
        this.source = source;
        this.value = value;
    }

    @Override
    public TripleSource source() {
        return this.source;
    }

    @Override
    public T value() {
        return this.value;
    }

}
