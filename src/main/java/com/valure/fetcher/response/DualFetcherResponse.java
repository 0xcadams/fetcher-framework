package com.valure.fetcher.response;

import com.valure.fetcher.model.GsonableData;

public class DualFetcherResponse<T> extends GsonableData {

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

    public DualSource source() {
        return this.source;
    }

    public T value() {
        return this.value;
    }

}
