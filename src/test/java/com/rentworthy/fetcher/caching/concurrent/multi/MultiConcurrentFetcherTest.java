/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.rentworthy.fetcher.caching.concurrent.multi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.FetcherFactory;
import com.rentworthy.fetcher.caching.concurrent.TestExecutorServiceCachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;

public class MultiConcurrentFetcherTest {

    @Test
    public void cachingFetcherWrapperTest() {

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> "test");

        try {
            Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch()).isEqualTo("test");
        }
        catch (final FetcherException e) {
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperNullTest() {

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> null);

        try {
            fetcher.fetch();
            Assertions.assertThat(fetcher.fetch()).isNull();
        }
        catch (final FetcherException e) {
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperFetcherExceptionTest() {

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> {
            throw new FetcherException(new RuntimeException());
        });

        try {
            fetcher.fetch();
            Assert.fail();
        }
        catch (final FetcherException e) {
            Assert.assertEquals(e.getCause().getCause().getClass(), FetcherException.class);
        }

    }

    @Test
    public void cachingFetcherWrapperSleepTest() {

        final int timeWait = 300;

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> {

            try {
                Thread.sleep(timeWait); // do something time-consuming
            }
            catch (final InterruptedException e) {
                throw new FetcherException(e);
            }

            return "";

        });

        try {
            fetcher.fetch();
        }
        catch (final FetcherException e) {
            Assert.fail();
        }

        try {
            Thread.sleep(timeWait * 2); // do something time-consuming
        }
        catch (final InterruptedException e) {
            Assertions.fail(e.getMessage());
        }

        try {

            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperDoubleFetcherExceptionTest() {

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> {
            throw new FetcherException(new RuntimeException());
        });

        try {
            fetcher.fetch();
            Assert.fail();
        }
        catch (final FetcherException e) {

            // System.o

            Assert.assertEquals(e.getCause().getCause().getCause().getClass(),
                RuntimeException.class);

            try {
                fetcher.fetch();
                Assert.fail();
            }
            catch (final FetcherException e1) {
                Assert.assertEquals(e.getClass(), e1.getClass());
                Assert.assertEquals(e.getCause().getClass(), e1.getCause().getClass());
            }

        }

    }

    @Test
    public void cachingMultiThreadedFetcherClearObjWrapperTest() {

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> {
            return "test_ret";
        });

        final List<Future<String>> futures = new ArrayList<>();

        final TestExecutorServiceCachingFetcher exec = new TestExecutorServiceCachingFetcher();

        for (int i = 0; i < 100; i++) {

            try {

                final Future<String> future = exec.fetch().submit(() -> {

                    Thread.sleep(1);

                    try {

                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");

                    }
                    catch (final FetcherException e) {
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
