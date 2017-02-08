package com.valure.fetcher.exception;

public class FetcherNotReadyException extends FetcherException {

    private static final long serialVersionUID = 876664429781289366L;

    public FetcherNotReadyException(final Throwable val) {
        super(val);
    }

    public FetcherNotReadyException(final String string) {
        super(string);
    }

}
