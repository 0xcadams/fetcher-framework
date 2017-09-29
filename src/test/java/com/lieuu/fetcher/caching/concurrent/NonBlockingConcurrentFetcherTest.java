/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.lieuu.fetcher.caching.concurrent;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.exception.FetcherNotReadyException;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

//public class NonBlockingConcurrentFetcherTest {
//
//  @Test
//  public void cachingFetcherWrapperTest() {
//
//    final Fetcher<String> fetcher = Fetchers.getNonBlockingConcurrentFetcher(() -> "test");
//
//    try {
//      Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
//      Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
//      Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
//      Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
//      Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
//    }
//    catch (final FetcherException e) {
//      Assert.fail();
//    }
//
//  }
//
//  @Test
//  public void cachingFetcherWrapperNullTest() {
//
//    final Fetcher<String> fetcher = Fetchers.getNonBlockingConcurrentFetcher(() -> null);
//
//    try {
//      fetcher.fetch();
//      Assertions.assertThat(fetcher.fetch()).isNull();
//    }
//    catch (final FetcherException e) {
//      Assert.fail();
//    }
//
//  }
//
//  @Test
//  public void cachingFetcherWrapperFetcherExceptionTest() {
//
//    final Fetcher<String> fetcher = Fetchers.getNonBlockingConcurrentFetcher(() -> {
//      throw new FetcherException(new RuntimeException());
//    });
//
//    try {
//      fetcher.fetch();
//      Assert.fail();
//    }
//    catch (final FetcherException e) {
//      Assert.assertEquals(e.getCause().getCause().getClass(), FetcherException.class);
//    }
//
//  }
//
//  @Test
//  public void cachingFetcherWrapperSleepTest() {
//
//    final int timeWait = 1000;
//
//    final Fetcher<String> fetcher = Fetchers.getNonBlockingConcurrentFetcher(() -> {
//
//      try {
//        Thread.sleep(timeWait); // do something time-consuming
//      }
//      catch (final InterruptedException e) {
//        throw new FetcherException(e);
//      }
//
//      return "";
//
//    });
//
//    try {
//      fetcher.fetch();
//      Assert.fail();
//    }
//    catch (final FetcherException e) {
//      Assertions.assertThat(e.getClass()).isEqualTo(FetcherNotReadyException.class);
//    }
//
//    try {
//      Thread.sleep(timeWait + 50); // do something time-consuming
//    }
//    catch (final InterruptedException e) {
//      Assertions.fail(e.getMessage());
//    }
//
//    try {
//
//      Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
//      Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
//      Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
//      Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
//      Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
//      Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
//
//    }
//    catch (final FetcherException e) {
//      e.printStackTrace();
//      Assert.fail();
//    }
//
//  }
//
  // @Test
  // public void cachingFetcherWrapperDoubleFetcherExceptionTest() {
  //
  // final ConcurrentCachingFetcherWrapper<String> fetcher = new
  // ConcurrentCachingFetcherWrapper<>(10,
  // new ImmediateConcurrentFetcherWrapper<>(() -> {
  // throw new FetcherException(new RuntimeException());
  // }));
  //
  // try {
  // fetcher.fetch();
  // Assert.fail();
  // }
  // catch (final FetcherException e) {
  //
  // // System.o
  //
  // Assert.assertEquals(e.getCause().getCause().getCause().getClass(),
  // RuntimeException.class);
  //
  // try {
  // fetcher.fetch();
  // Assert.fail();
  // }
  // catch (final FetcherException e1) {
  // Assert.assertEquals(e.getClass(), e1.getClass());
  // Assert.assertEquals(e.getCause().getClass(), e1.getCause().getClass());
  // }
  //
  // }
  //
  // }
  //
  // @Test
  // public void cachingMultiThreadedFetcherClearObjWrapperTest() {
  //
  // final ConcurrentCachingFetcherWrapper<String> fetcher = new
  // ConcurrentCachingFetcherWrapper<String>(new
  // ImmediateConcurrentFetcherWrapper<>(() -> "test_ret"));
  //
  // final List<Future<String>> futures = new ArrayList<>();
  //
  // final ExecutorServiceCachingFetcher exec = new
  // ExecutorServiceCachingFetcher();
  //
  // for (int i = 0; i < 100; i++) {
  //
  // try {
  //
  // final Future<String> future = exec.fetch().submit(() -> {
  //
  // Thread.sleep(1);
  //
  // try {
  //
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
  // Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
  // Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
  //
  // }
  // catch (final FetcherException e) {
  // if (!e.getCause().getClass().equals(FetcherNotReadyException.class)) {
  // Assertions.fail(e.getMessage());
  // }
  // }
  //
  // return "";
  //
  // });
  //
  // futures.add(future);
  //
  // }
  // catch (final FetcherException e) {
  // Assertions.fail(e.getMessage());
  // }
  //
  // }
  //
  // for (final Future<String> future : futures) {
  // try {
  // future.get();
  // }
  // catch (InterruptedException | ExecutionException e) {
  // e.printStackTrace();
  // Assertions.fail(e.getMessage());
  // }
  // }
  //
  // }
//
//}
