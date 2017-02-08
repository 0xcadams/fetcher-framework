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

import org.junit.Assert;
import org.junit.Test;

import com.valure.fetcher.concurrent.ExecutorServiceCachingFetcher;
import com.valure.fetcher.exception.FetcherException;
import com.valure.fetcher.exception.FetcherNotReadyException;

public class ConcurrentCachingFetcherWrapperTest {

    @Test
    public void testConcurrentCachingFetcherWrapper() {

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {
                return "test";
            }

        }));

    }

    @Test
    public void cachingFetcherWrapperTest() {

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {
                return "test";
            }

        }));

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

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {
                return null;
            }

        }));

        try {
            fetcher.fetch();
            Assert.fail();
        } catch (final FetcherException e) {

        }

    }

    @Test
    public void cachingFetcherWrapperFetcherExceptionTest() {

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {
                throw new FetcherException(new RuntimeException());
            }

        }));

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

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {

                try {
                    Thread.sleep(timeWait); // do something time-consuming
                } catch (final InterruptedException e) {
                    throw new FetcherException(e);
                }

                return "";

            }

        }));

        try {
            fetcher.fetch();
            Assert.fail();
        } catch (final FetcherException e) {
            Assert.assertEquals(e.getCause().getClass(), FetcherNotReadyException.class);
        }

        try {
            Thread.sleep(timeWait * 2); // do something time-consuming
        } catch (final InterruptedException e) {
            fail(e.getMessage());
        }

        try {

            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());
            Assert.assertEquals(fetcher.fetch(), fetcher.fetch());

        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void cachingFetcherWrapperDoubleFetcherExceptionTest() {

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {
                throw new FetcherException(new RuntimeException());
            }

        }));

        try {
            fetcher.fetch();
            Assert.fail();
        } catch (final FetcherException e) {

            //            System.o

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

        final ConcurrentCachingFetcherWrapper<String> fetcher = new ConcurrentCachingFetcherWrapper<>(new ConcurrentFetcherWrapper<>(new Fetcher<String>() {

            @Override
            public String fetch() throws FetcherException {
                return "test_ret";
            }

        }));

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
                            if (!e.getCause().getClass().equals(FetcherNotReadyException.class)) {
                                fail(e.getMessage());
                            }
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
