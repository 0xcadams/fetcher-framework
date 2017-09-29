/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.lieuu.fetcher.concurrent;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.exception.FetcherException;

import java.util.concurrent.Callable;

public class FetcherCallable<T> implements Callable<T> {

  private final Fetcher<T> fetcher;

  /**
   * @param fetcher
   */
  public FetcherCallable(final Fetcher<T> fetcher) {
    this.fetcher = fetcher;
  }

  /*
   * (non-Javadoc)
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public final T call() throws FetcherException {
    return this.fetcher.fetch();
  }

}
