package com.rentworthy.fetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.caching.concurrent.TestExecutorServiceCachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;

public class CachingFetcherTest {

    @Test
    public void cachingFetcherWrapperTest() {

        final Fetcher<String> fetcher = Fetchers.getCachingFetcher(() -> "test");

        try {
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperNullTest() {

        final Fetcher<String> fetcher = Fetchers.getCachingFetcher(() -> null);

        try {
            fetcher.fetch();
            Assert.fail();
        }
        catch (final FetcherException e) {

        }

    }

    @Test
    public void cachingFetcherWrapperNullPointerExceptionTest() {

        final Fetcher<String> fetcher = Fetchers.getCachingFetcher(new Fetcher<String>() {

            @SuppressWarnings ("null")
            @Override
            public String fetch() throws FetcherException {
                final String test = null;
                test.equals(null);
                return test;
            }

        });

        try {
            fetcher.fetch();
            Assert.fail();
        }
        catch (final FetcherException e) {
            Assert.assertEquals(e.getCause().getClass(), NullPointerException.class);
        }

    }

    @Test
    public void cachingFetcherWrapperFetcherExceptionTest() {

        final Fetcher<String> fetcher = Fetchers.getCachingFetcher(() -> {
            throw new FetcherException(new NullPointerException());
        });

        try {
            fetcher.fetch();
            Assert.fail();
        }
        catch (final FetcherException e) {
            Assert.assertEquals(e.getCause().getClass(), FetcherException.class);
        }

    }

    @Test
    public void cachingFetcherWrapperSleepTest() {

        final int timeWait = 300;

        final Fetcher<String> fetcher = Fetchers.getCachingFetcher(() -> {

            try {
                Thread.sleep(timeWait); // do something time-consuming
            }
            catch (final InterruptedException e) {
                throw new FetcherException(e);
            }

            final StringBuffer sb = new StringBuffer();

            for (int i = 0; i < 100000; i++) {
                sb.append("this is a very long string");
            }

            return sb.toString();

        });

        try {

            final long startTime = System.currentTimeMillis();

            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());

            Assert.assertTrue((System.currentTimeMillis() - startTime) < (timeWait * 2));

        }
        catch (final FetcherException e) {
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperDoubleFetcherExceptionTest() {

        final Fetcher<String> fetcher = Fetchers.getCachingFetcher(() -> {
            throw new FetcherException(new NullPointerException());
        });

        try {
            fetcher.fetch();
            Assert.fail();
        }
        catch (final FetcherException e) {

            Assert.assertEquals(e.getCause().getClass(), FetcherException.class);

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

        final CachingFetcher<String> fetcher = (CachingFetcher<String>) Fetchers.getCachingFetcher(
            () -> {
                try {
                    Thread.sleep(10);
                }
                catch (final InterruptedException e) {
                    Assert.fail();
                }
                return "test_ret";
            });

        final List<Future<String>> futures = new ArrayList<>();

        final TestExecutorServiceCachingFetcher exec = new TestExecutorServiceCachingFetcher();

        for (int i = 0; i < 100000; i++) {

            try {

                final Future<String> future = exec.fetch().submit(() -> {

                    try {

                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        fetcher.clearCachedObject();
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");
                        Assertions.assertThat(fetcher.fetch()).isEqualTo("test_ret");

                    }
                    catch (final FetcherException e) {
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
