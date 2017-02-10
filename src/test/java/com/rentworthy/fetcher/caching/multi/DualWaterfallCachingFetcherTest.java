package com.rentworthy.fetcher.caching.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.MultiFetcher;
import com.rentworthy.fetcher.caching.CachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;

public class DualWaterfallCachingFetcherTest {

    @Test
    public void dualWaterfallCachingFetcherTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assert.fail(),
                                                                                 new CachingFetcher<String>(() -> "test"),
                                                                                 new CachingFetcher<String>(() -> {
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
    public void dualWaterfallCachingFetcherBackupTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains("should never reach here!"), new CachingFetcher<String>(() -> {
                throw new FetcherException("should never reach here!");
            }), new CachingFetcher<String>(() -> "test"));

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
    public void dualWaterfallCachingFetcherAllFailingTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains("should never reach here!"), new CachingFetcher<String>(() -> {
                throw new FetcherException("should never reach here!");
            }), new CachingFetcher<String>(() -> {
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
    public void dualWaterfallCachingFetcherTimingTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains("should never reach here!"), new CachingFetcher<String>(() -> {

                try {
                    Thread.sleep(1000);
                }
                catch (final InterruptedException e) {
                    throw new FetcherException(e);
                }

                throw new FetcherException("should never reach here!");

            }), new CachingFetcher<String>(() -> "test"));

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
    public void dualWaterfallCachingFetcherTimingBackupTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains("should never reach here!"), new CachingFetcher<String>(() -> {

                try {
                    Thread.sleep(1000);
                }
                catch (final InterruptedException e) {
                    throw new FetcherException(e);
                }

                return "test";

            }), new CachingFetcher<String>(() -> {

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
