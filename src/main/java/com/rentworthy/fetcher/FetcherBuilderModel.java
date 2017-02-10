package com.rentworthy.fetcher;

public class FetcherBuilderModel {

    private final boolean concurrent;
    private final boolean value;
    private final long expiring;

    /**
     * @param concurrent
     * @param value
     * @param expiring
     */
    public FetcherBuilderModel(final boolean concurrent, final boolean value, final long expiring) {
        this.concurrent = concurrent;
        this.value = value;
        this.expiring = expiring;
    }

    /**
     * Gets the value of concurrent.
     *
     * @return concurrent of type boolean
     */
    public boolean isConcurrent() {
        return this.concurrent;
    }

    /**
     * Gets the value of value.
     *
     * @return value of type boolean
     */
    public boolean isValue() {
        return this.value;
    }

    /**
     * Gets the value of expiring.
     *
     * @return expiring of type long
     */
    public long getExpiring() {
        return this.expiring;
    }

}
