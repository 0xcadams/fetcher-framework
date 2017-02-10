package com.rentworthy.fetcher.caching.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.MultiFetcher;
import com.rentworthy.fetcher.caching.CachingFetcherWrapper;
import com.rentworthy.fetcher.exception.FetcherException;

public class DualWaterfallCachingFetcherWrapperTest {

    @Test
    public void dualWaterfallCachingFetcherWrapperTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assert.fail(),
                                                                                        new CachingFetcherWrapper<String>(() -> "test"),
                                                                                        new CachingFetcherWrapper<String>(() -> {
                                                                                            throw new FetcherException("should never reach here!");
                                                                                        }));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void dualWaterfallCachingFetcherWrapperBackupTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> "test"));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void dualWaterfallCachingFetcherWrapperAllFailingTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should reach here!");
                }));

        try {
            fetcher.fetch().value();
            Assert.fail(); // should not reach this
        }
        catch (final FetcherException e) {
            Assertions.assertThat(e.getMessage()).contains("should reach here!");
        }

    }

    @Test
    public void dualWaterfallCachingFetcherWrapperTimingTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcherWrapper<String>(() -> {

                    try {
                        Thread.sleep(1000);
                    }
                    catch (final InterruptedException e) {
                        throw new FetcherException(e);
                    }

                    throw new FetcherException("should never reach here!");

                }), new CachingFetcherWrapper<String>(() -> "test"));

        try {

            final long firstStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            final long firstTime = System.currentTimeMillis() - firstStartTime;

            final long secStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            Assertions.assertThat(firstTime).isGreaterThan(
                secStartTime - System.currentTimeMillis());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void dualWaterfallCachingFetcherWrapperTimingBackupTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcherWrapper<String>(() -> {

                    try {
                        Thread.sleep(1000);
                    }
                    catch (final InterruptedException e) {
                        throw new FetcherException(e);
                    }

                    return "test";

                }), new CachingFetcherWrapper<String>(() -> {

                    Assert.fail();
                    return "";

                }));

        try {

            final long firstStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            final long firstTime = System.currentTimeMillis() - firstStartTime;

            final long secStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            Assertions.assertThat(firstTime).isGreaterThan(
                secStartTime - System.currentTimeMillis());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
