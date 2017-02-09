/**
 * @author cadams2
 * @since Feb 8, 2017
 */
package com.rentworthy.fetcher.caching.concurrent.multi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.MultiFetcher;
import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.exception.FetcherNotReadyException;

public class CachingNonBlockingConcurrentFetcherWrapperTest {

    @Test
    public void cachingFetcherWrapperTest() {

        final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<String>(new NonBlockingConcurrentFetcherWrapper<>(() -> "test"));

        try {
            Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test");
            Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test");
        }
        catch (final FetcherException e) {
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperNullTest() {

        final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<>(new NonBlockingConcurrentFetcherWrapper<>(() -> null));

        try {
            fetcher.fetch().value();
            assertThat(fetcher.fetch().value()).isNull();
        }
        catch (final FetcherException e) {
            fail();
        }

    }

    @Test
    public void cachingFetcherWrapperFetcherExceptionTest() {

        final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<>(10,
                                                                                                      new NonBlockingConcurrentFetcherWrapper<>(() -> {
                                                                                                          throw new FetcherException(new RuntimeException());
                                                                                                      }));

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

        final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<>(new NonBlockingConcurrentFetcherWrapper<>(() -> {

            try {
                Thread.sleep(timeWait); // do something time-consuming
            }
            catch (final InterruptedException e) {
                throw new FetcherException(e);
            }

            return "";

        }));

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

            Assert.assertEquals(fetcher.fetch().value(), fetcher.fetch().value());
            Assert.assertEquals(fetcher.fetch().value(), fetcher.fetch().value());
            Assert.assertEquals(fetcher.fetch().value(), fetcher.fetch().value());
            Assert.assertEquals(fetcher.fetch().value(), fetcher.fetch().value());
            Assert.assertEquals(fetcher.fetch().value(), fetcher.fetch().value());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperDoubleFetcherExceptionTest() {

        final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<>(10,
                                                                                                      new NonBlockingConcurrentFetcherWrapper<>(() -> {
                                                                                                          throw new FetcherException(new RuntimeException());
                                                                                                      }));

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

        final CachingNonBlockingConcurrentFetcherWrapper<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<String>(new NonBlockingConcurrentFetcherWrapper<>(() -> {
            return "test_ret";
        }));

        final List<Future<String>> futures = new ArrayList<>();

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        for (int i = 0; i < 100; i++) {

            try {

                final Future<String> future = exec.fetch().submit(() -> {

                    Thread.sleep(1);

                    try {

                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);
                        Assertions.assertThat(fetcher.fetch().value()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.clearFuture()).isEqualTo(true);

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
