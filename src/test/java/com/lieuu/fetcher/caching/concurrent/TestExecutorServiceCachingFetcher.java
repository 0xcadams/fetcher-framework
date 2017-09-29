package com.lieuu.fetcher.caching.concurrent;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestExecutorServiceCachingFetcher implements Fetcher<ExecutorService> {

  private static final Fetcher<ExecutorService> FETCHER = Fetchers.getCachingFetcher(
    () -> Executors.newCachedThreadPool());

  @Override
  public ExecutorService fetch() throws FetcherException {
    return TestExecutorServiceCachingFetcher.FETCHER.fetch();
  }

}
