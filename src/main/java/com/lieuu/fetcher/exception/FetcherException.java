package com.lieuu.fetcher.exception;

public class FetcherException extends Exception {

    private static final long serialVersionUID = -2661821175451588708L;

    /**
     * Exception to wrap any Throwable from fetch() implementation.
     *
     * @param val
     *            throwable caught in fetch
     */
    public FetcherException(final Throwable val) {
        super(val);
    }

    /**
     * Custom exception to create from fetch() implementation.
     *
     * @param string
     *            custom string from fetch error
     */
    public FetcherException(final String string) {
        super(string);
    }

}
