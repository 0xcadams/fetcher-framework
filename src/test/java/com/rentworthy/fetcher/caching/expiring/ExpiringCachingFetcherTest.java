package com.rentworthy.fetcher.caching.expiring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.Fetchers;
import com.rentworthy.fetcher.caching.concurrent.TestExecutorServiceCachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;

public class ExpiringCachingFetcherTest {

    @Test
    public void testExpiringCachingFetcherWrapper() {

        final AtomicInteger count = new AtomicInteger(0);

        final Fetcher<Integer> cachingFetcher = Fetchers.getExpiringCachingFetcher(
            (Fetcher<Integer>) () -> count.incrementAndGet(), 50);

        try {

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(1);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(2);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(3);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(4);
            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(4);

        }
        catch (FetcherException | InterruptedException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    public void testMultiThreadedExpiringCachingFetcherWrapper() {

        final int maxTimeMs = 400;

        final AtomicInteger count = new AtomicInteger(0);

        final Fetcher<Integer> expire = Fetchers.getExpiringCachingFetcher(
            (Fetcher<Integer>) () -> count.incrementAndGet(), maxTimeMs);

        final TestExecutorServiceCachingFetcher exec = new TestExecutorServiceCachingFetcher();

        final List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < maxTimeMs; i++) { // run the same number of threads
                                              // as ms wait time

            try {

                final Future<String> future = exec.fetch().submit(() -> {

                    try {

                        for (int countRuns = 1; countRuns <= 10; countRuns++) {

                            Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);
                            Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);
                            Assertions.assertThat(expire.fetch()).isEqualTo(countRuns);

                            Thread.sleep(maxTimeMs + 50);

                        }

                    }
                    catch (FetcherException | InterruptedException e) {
                        Assertions.fail(e.getMessage());
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
