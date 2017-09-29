/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.lieuu.fetcher.caching.concurrent.multi.expiring;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.caching.concurrent.TestExecutorServiceCachingFetcher;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.exception.FetcherNotReadyException;
import org.assertj.core.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ExpiringMultiConcurrentFetcherTest {

  // @Test
  public void testExpiringConcurrentCachingFetcherWrapper() {

    final int maxTimeMs = 500;

    final AtomicInteger count = new AtomicInteger(0);

    final Fetcher<Integer> expire = Fetchers.getExpiringMultiConcurrentFetcher(maxTimeMs,
      () -> count.incrementAndGet());

    try {

      Assertions.assertThat(expire.fetch()).isEqualTo(1);
      Assertions.assertThat(expire.fetch()).isEqualTo(1);
      Assertions.assertThat(expire.fetch()).isEqualTo(1);
      Assertions.assertThat(expire.fetch()).isEqualTo(1);

      Thread.sleep(maxTimeMs + 5);

      Assertions.assertThat(expire.fetch()).isEqualTo(2);
      Assertions.assertThat(expire.fetch()).isEqualTo(2);
      Assertions.assertThat(expire.fetch()).isEqualTo(2);
      Assertions.assertThat(expire.fetch()).isEqualTo(2);

      Thread.sleep(maxTimeMs + 5);

      Assertions.assertThat(expire.fetch()).isEqualTo(3);
      Assertions.assertThat(expire.fetch()).isEqualTo(3);
      Assertions.assertThat(expire.fetch()).isEqualTo(3);
      Assertions.assertThat(expire.fetch()).isEqualTo(3);
      Assertions.assertThat(expire.fetch()).isEqualTo(3);

      Thread.sleep(maxTimeMs * 5);

      Assertions.assertThat(expire.fetch()).isEqualTo(4);
      Assertions.assertThat(expire.fetch()).isEqualTo(4);
      Assertions.assertThat(expire.fetch()).isEqualTo(4);

    }
    catch (FetcherException | InterruptedException e) {
      Assertions.fail(e.getMessage());
    }

  }

  // @Test
  // public void testNeverExpiringConcurrentCachingFetcherWrapper() {
  //
  // final int waitTimeMs = 500;
  //
  // final AtomicInteger count = new AtomicInteger(0);
  //
  // final Fetcher<Integer> expire =
  // FetcherFactory.getNeverExpiringMultiConcurrentFetcher(
  // () -> count.incrementAndGet());
  //
  // try {
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // Thread.sleep(waitTimeMs + 5);
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // Thread.sleep(waitTimeMs + 5);
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // Thread.sleep(waitTimeMs * 5);
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // }
  // catch (FetcherException | InterruptedException e) {
  // Assertions.fail(e.getMessage());
  // }
  //
  // }
  //
  // @Test
  // public void
  // testMultiThreadedNeverExpiringConcurrentCachingFetcherWrapper() {
  //
  // final int waitTimeMs = 500;
  //
  // final AtomicInteger count = new AtomicInteger(0);
  //
  // final Fetcher<Integer> expire = new MultiFetcherValue<>(new
  // ExpiringMultiConcurrentFetcher<Integer>(new
  // NonBlockingConcurrentFetcher<>(() -> count.incrementAndGet())));
  //
  // final ExecutorServiceCachingFetcher exec = new
  // ExecutorServiceCachingFetcher();
  //
  // final List<Future<String>> futures = new ArrayList<>();
  //
  // for (int i = 0; i < 5000; i++) {
  //
  // try {
  //
  // final Future<String> future = exec.fetch().submit(() -> {
  //
  // try {
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // Thread.sleep(waitTimeMs + 5);
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // Thread.sleep(waitTimeMs + 5);
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // Thread.sleep(waitTimeMs * 5);
  //
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  // Assertions.assertThat(expire.fetch()).isEqualTo(1);
  //
  // }
  // catch (FetcherException | InterruptedException e) {
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

  // @Test
  public void testMultiThreadedExpiringConcurrentCachingFetcherWrapper() {

    final int maxTimeMs = 1500;

    final AtomicInteger count = new AtomicInteger(0);

    final Fetcher<Integer> expire = Fetchers.getExpiringMultiConcurrentFetcher(maxTimeMs,
      () -> count.incrementAndGet());

    final TestExecutorServiceCachingFetcher exec = new TestExecutorServiceCachingFetcher();

    final List<Future<String>> futures = new ArrayList<>();

    for (int i = 0; i < maxTimeMs; i++) { // run the same number of threads
      // as ms wait time

      try {

        final Future<String> future = exec.fetch().submit(() -> {

          try {

            for (int countRuns = 1; countRuns <= 4; countRuns++) {

              Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);
              Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);
              Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);
              Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);

              Thread.sleep(maxTimeMs + 50);

            }

          }
          catch (FetcherException | InterruptedException e) {
            System.out.println("class was: " + e.getClass());
            if (!e.getCause().getClass().equals(FetcherNotReadyException.class)) {
              Assertions.fail(e.getMessage());
            }
          }

          return "";

        });

        futures.add(future);

      }
      catch (final FetcherException e) {
        Assertions.fail(e.getMessage());
      }

    }

    for (final Future<String> future : futures) {

      try {
        future.get();
      }
      catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
        Assertions.fail(e.getMessage());
      }

    }

  }

}
