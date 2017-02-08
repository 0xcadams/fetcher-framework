/**
 * @author  cadams2
 * @since   Feb 8, 2017
 */
package com.valure.fetcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.valure.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.valure.fetcher.exception.FetcherException;

public class ExpiringConcurrentCachingFetcherWrapperTest {

    @Test
    public void testExpiringConcurrentCachingFetcherWrapper() {

        final int maxTimeMs = 500;

        final AtomicInteger count = new AtomicInteger(0);

        Fetcher<Integer> expire = new ExpiringConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        }), maxTimeMs);

        try {

            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);

            Thread.sleep(maxTimeMs + 5);

            assertThat(expire.fetch()).isEqualTo(2);
            assertThat(expire.fetch()).isEqualTo(2);

            Thread.sleep(maxTimeMs + 5);

            assertThat(expire.fetch()).isEqualTo(3);
            assertThat(expire.fetch()).isEqualTo(3);

            Thread.sleep(maxTimeMs * 5);

            assertThat(expire.fetch()).isEqualTo(4);
            assertThat(expire.fetch()).isEqualTo(4);

        } catch (FetcherException | InterruptedException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testNeverExpiringConcurrentCachingFetcherWrapper() {

        final int waitTimeMs = 500;

        final AtomicInteger count = new AtomicInteger(0);

        Fetcher<Integer> expire = new ExpiringConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        }));

        try {

            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);

            Thread.sleep(waitTimeMs + 5);

            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);

            Thread.sleep(waitTimeMs + 5);

            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);

            Thread.sleep(waitTimeMs * 5);

            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);
            assertThat(expire.fetch()).isEqualTo(1);

        } catch (FetcherException | InterruptedException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testMultiThreadedNeverExpiringConcurrentCachingFetcherWrapper() {

        final int waitTimeMs = 500;

        final AtomicInteger count = new AtomicInteger(0);

        Fetcher<Integer> expire = new ExpiringConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        }));

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        final List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {

            try {

                final Future<String> future = exec.fetch().submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {

                        try {

                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);

                            Thread.sleep(waitTimeMs + 5);

                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);

                            Thread.sleep(waitTimeMs + 5);

                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);

                            Thread.sleep(waitTimeMs * 5);

                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);
                            assertThat(expire.fetch()).isEqualTo(1);

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

    @Test
    public void testMultiThreadedExpiringConcurrentCachingFetcherWrapper() {

        final int maxTimeMs = 2000;

        final AtomicInteger count = new AtomicInteger(0);

        Fetcher<Integer> expire = new ExpiringConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<Integer>() {

            @Override
            public Integer fetch() throws FetcherException {
                return count.incrementAndGet();
            }

        }), maxTimeMs);

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        final List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < maxTimeMs; i++) { // run the same number of threads as ms wait time

            try {

                final Future<String> future = exec.fetch().submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {

                        try {

                            for (int countRuns = 1; countRuns <= 20; countRuns++) {

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
