package com.fetcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.fetcher.ExpiringCachingFetcherWrapper;
import com.fetcher.Fetcher;
import com.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.fetcher.exception.FetcherException;

public class ExpiringCachingFetcherWrapperTest {

    @Test
    public void testNeverExpiringCachingFetcherWrapper() {

        final AtomicInteger count = new AtomicInteger(0);

        final Fetcher<Integer> cachingFetcher = new ExpiringCachingFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        });

        try {

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(1);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(1);

        } catch (FetcherException | InterruptedException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    public void testExpiringCachingFetcherWrapper() {

        final AtomicInteger count = new AtomicInteger(0);

        final Fetcher<Integer> cachingFetcher = new ExpiringCachingFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        }, 50);

        try {

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(1);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(2);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(3);

            Thread.sleep(200);

            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(4);
            Assertions.assertThat(cachingFetcher.fetch()).isEqualTo(4);

        } catch (FetcherException | InterruptedException e) {
            Assertions.fail(e.getMessage());
        }

    }
    

    @Test
    public void testMultiThreadedExpiringCachingFetcherWrapper() {

        final int maxTimeMs = 1000;

        final AtomicInteger count = new AtomicInteger(0);

        Fetcher<Integer> expire = new ExpiringCachingFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        }, maxTimeMs);

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        final List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < maxTimeMs; i++) { // run the same number of threads as ms wait time

            try {

                final Future<String> future = exec.fetch().submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {

                        try {

                            for (int countRuns = 1; countRuns <= 10; countRuns++) {

                                assertThat(expire.fetch()).isEqualTo(countRuns);
                                assertThat(expire.fetch()).isEqualTo(countRuns);
                                assertThat(expire.fetch()).isEqualTo(countRuns);

                                Thread.sleep(maxTimeMs + 50);

                            }

                        } catch (FetcherException | InterruptedException e) {
                            fail(e.getMessage());
                        }

                        return "";

                    }

                });

                futures.add(future);

            } catch (FetcherException e) {
                fail(e.getMessage());
            }

        }

        for (Future<String> future : futures) {

            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                fail(e.getMessage());
            }

        }

    }

}
