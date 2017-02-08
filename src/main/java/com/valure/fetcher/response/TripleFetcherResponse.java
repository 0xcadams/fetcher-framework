package com.valure.fetcher.response;

import com.valure.fetcher.model.GsonableData;

public class TripleFetcherResponse<T> extends GsonableData {

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

    public TripleSource source() {
        return this.source;
    }

    public T value() {
        return this.value;
    }

}
