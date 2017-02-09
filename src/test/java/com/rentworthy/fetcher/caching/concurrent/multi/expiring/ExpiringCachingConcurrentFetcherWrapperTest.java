/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.rentworthy.fetcher.caching.concurrent.multi.expiring;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.caching.concurrent.multi.expiring.ExpiringCachingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;
import com.rentworthy.fetcher.response.MultiFetcher;

public class ExpiringCachingConcurrentFetcherWrapperTest {

    @Test
    public void testExpiringConcurrentCachingFetcherWrapper() {

        final int maxTimeMs = 500;

        final AtomicInteger count = new AtomicInteger(0);

        final MultiFetcher<Integer> expire = new ExpiringCachingConcurrentFetcherWrapper<Integer>(new NonBlockingConcurrentFetcherWrapper<>(() -> count.incrementAndGet()),
                                                                                                              maxTimeMs);

        try {

            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

            Thread.sleep(maxTimeMs + 5);

            Assertions.assertThat(expire.fetch().value()).isEqualTo(2);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(2);

            Thread.sleep(maxTimeMs + 5);

            Assertions.assertThat(expire.fetch().value()).isEqualTo(3);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(3);

            Thread.sleep(maxTimeMs * 5);

            Assertions.assertThat(expire.fetch().value()).isEqualTo(4);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(4);

        }
        catch (FetcherException | InterruptedException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    public void testNeverExpiringConcurrentCachingFetcherWrapper() {

        final int waitTimeMs = 500;

        final AtomicInteger count = new AtomicInteger(0);

        final MultiFetcher<Integer> expire = new ExpiringCachingConcurrentFetcherWrapper<Integer>(new NonBlockingConcurrentFetcherWrapper<>(() -> count.incrementAndGet()));

        try {

            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

            Thread.sleep(waitTimeMs + 5);

            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

            Thread.sleep(waitTimeMs + 5);

            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

            Thread.sleep(waitTimeMs * 5);

            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
            Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

        }
        catch (FetcherException | InterruptedException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    public void testMultiThreadedNeverExpiringConcurrentCachingFetcherWrapper() {

        final int waitTimeMs = 500;

        final AtomicInteger count = new AtomicInteger(0);

        final MultiFetcher<Integer> expire = new ExpiringCachingConcurrentFetcherWrapper<Integer>(new NonBlockingConcurrentFetcherWrapper<>(() -> count.incrementAndGet()));

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        final List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {

            try {

                final Future<String> future = exec.fetch().submit(() -> {

                    try {

                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

                        Thread.sleep(waitTimeMs + 5);

                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

                        Thread.sleep(waitTimeMs + 5);

                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

                        Thread.sleep(waitTimeMs * 5);

                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);
                        Assertions.assertThat(expire.fetch().value()).isEqualTo(1);

                    }
                    catch (FetcherException | InterruptedException e) {
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

    @Test
    public void testMultiThreadedExpiringConcurrentCachingFetcherWrapper() {

        final int maxTimeMs = 800;

        final AtomicInteger count = new AtomicInteger(0);

        final MultiFetcher<Integer> expire = new ExpiringCachingConcurrentFetcherWrapper<Integer>(new NonBlockingConcurrentFetcherWrapper<>(() -> count.incrementAndGet()),
                                                                                                              maxTimeMs);

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        final List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < maxTimeMs; i++) { // run the same number of threads
                                              // as ms wait time

            try {

                final Future<String> future = exec.fetch().submit(() -> {

                    try {

                        for (int countRuns = 1; countRuns <= 10; countRuns++) {

                            Assertions.assertThat(expire.fetch().value()).isEqualTo(countRuns);
                            Assertions.assertThat(expire.fetch().value()).isEqualTo(countRuns);
                            Assertions.assertThat(expire.fetch().value()).isEqualTo(countRuns);
                            Assertions.assertThat(expire.fetch().value()).isEqualTo(countRuns);

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
