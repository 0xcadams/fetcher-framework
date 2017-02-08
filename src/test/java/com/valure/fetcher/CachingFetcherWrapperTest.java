package com.valure.fetcher;

import org.junit.Assert;
import org.junit.Test;

import com.valure.fetcher.exception.FetcherException;

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

}
