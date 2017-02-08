package com.fetcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Test;

import com.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.fetcher.exception.FetcherException;

public class CachingFetcherWrapperTest {

    @Test
    public void cachingFetcherWrapperTest() {

        final Fetcher<String> fetcher = new CachingFetcherWrapper<String>(() -> "test");

        try {
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
            Assert.assertEquals("test", fetcher.fetch());
        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperNullTest() {

        final Fetcher<String> fetcher = new CachingFetcherWrapper<String>(() -> null);

        try {
            fetcher.fetch();
            Assert.fail();
        } catch (final FetcherException e) {

        }

    }

    @Test
    public void cachingFetcherWrapperNullPointerExceptionTest() {

        final Fetcher<String> fetcher = new CachingFetcherWrapper<String>(new Fetcher<String>() {

            @SuppressWarnings("null")
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
        } catch (final FetcherException e) {
            Assert.assertEquals(e.getCause().getClass(), NullPointerException.class);
        }

    }

    @Test
    public void cachingFetcherWrapperFetcherExceptionTest() {

        final Fetcher<String> fetcher = new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException(new NullPointerException());
        });

        try {
            fetcher.fetch();
            Assert.fail();
        } catch (final FetcherException e) {
            Assert.assertEquals(e.getCause().getClass(), FetcherException.class);
        }

    }

    @Test
    public void cachingFetcherWrapperSleepTest() {

        final int timeWait = 300;

        final Fetcher<String> fetcher = new CachingFetcherWrapper<String>(() -> {

            try {
                Thread.sleep(timeWait); // do something time-consuming
            } catch (final InterruptedException e) {
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

        } catch (final FetcherException e) {
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperDoubleFetcherExceptionTest() {

        final Fetcher<String> fetcher = new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException(new NullPointerException());
        });

        try {
            fetcher.fetch();
            Assert.fail();
        } catch (final FetcherException e) {

            Assert.assertEquals(e.getCause().getClass(), FetcherException.class);

            try {
                fetcher.fetch();
                Assert.fail();
            } catch (final FetcherException e1) {
                Assert.assertEquals(e.getClass(), e1.getClass());
                Assert.assertEquals(e.getCause().getClass(), e1.getCause().getClass());
            }

        }

    }

    @Test
    public void cachingMultiThreadedFetcherClearObjWrapperTest() {

        final CachingFetcherWrapper<String> fetcher = new CachingFetcherWrapper<String>(() -> {
            return "test_ret";
        });

        final List<Future<String>> futures = new ArrayList<>();

        final ExecutorServiceCachingFetcher exec = new ExecutorServiceCachingFetcher();

        for (int i = 0; i < 1000000; i++) {

            try {

                final Future<String> future = exec.fetch().submit(new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        
                        Thread.sleep(1);

                        try {

                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.clearCachedObject()).isEqualTo(true);
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.clearCachedObject()).isEqualTo(true);
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.clearCachedObject()).isEqualTo(true);
                            assertThat(fetcher.fetch()).isEqualTo("test_ret");
                            assertThat(fetcher.clearCachedObject()).isEqualTo(true);

                        } catch (FetcherException e) {
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
                fail(e.getMessage());
            }
        }

    }

}
