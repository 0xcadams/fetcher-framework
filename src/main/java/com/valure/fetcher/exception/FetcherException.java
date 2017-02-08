package com.valure.fetcher.exception;

public class FetcherException extends Exception {

    private static final long serialVersionUID = -2661821175451588708L;

    public FetcherException(final Throwable val) {
        super(val);
    }

    public FetcherException(final String string) {
        super(string);
    }

}
