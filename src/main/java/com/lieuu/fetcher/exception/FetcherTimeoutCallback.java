package com.lieuu.fetcher.exception;

public interface FetcherTimeoutCallback {

  /**
   * Called when fetcher times out. This is not a fatal error and should not
   * be treated as such.
   *
   * @param fetcherNotReadyException
   */
  public void onTimeout(final FetcherNotReadyException fetcherNotReadyException);

}
