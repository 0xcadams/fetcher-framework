package com.lieuu.fetcher.exception;

import java.util.concurrent.TimeoutException;

public class FetcherNotReadyException extends FetcherException {

  private static final long serialVersionUID = -405797098949968173L;

  /**
   * FetcherException to wrap TimeoutException from concurrent fetch()
   * implementation.
   *
   * @param val TimeoutException caught in concurrent fetch
   */
  public FetcherNotReadyException(final TimeoutException val) {
    super(val);
  }

}
