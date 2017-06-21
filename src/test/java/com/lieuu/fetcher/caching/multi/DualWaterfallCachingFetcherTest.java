package com.lieuu.fetcher.caching.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;

public class DualWaterfallCachingFetcherTest {

    @Test
    public void dualWaterfallCachingFetcherTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(e -> Assert.fail(),
            (Fetcher<String>) () -> "test", (Fetcher<String>) () -> {
                throw new FetcherException("should never reach here!");
            });

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
    public void dualWaterfallCachingFetcherBackupTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (Fetcher<String>) () -> {
                throw new FetcherException("should never reach here!");
            }, (Fetcher<String>) () -> "test");

        try {
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
    public void dualWaterfallCachingFetcherAllFailingTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> {
                throw new FetcherException("should never reach here!");
            }), (() -> {
                throw new FetcherException("should reach here!");
            }));

        try {
            fetcher.fetch();
            Assert.fail(); // should not reach this
        }
        catch (final FetcherException e) {
            Assertions.assertThat(e.getMessage()).contains("should reach here!");
        }

    }

    @Test
    public void dualWaterfallCachingFetcherTimingTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> {

                try {
                    Thread.sleep(1000);
                }
                catch (final InterruptedException e) {
                    throw new FetcherException(e);
                }

                throw new FetcherException("should never reach here!");

            }), (() -> "test"));

        try {

            final long firstStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch());
            final long firstTime = System.currentTimeMillis() - firstStartTime;

            final long secStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch());
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

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> {

                try {
                    Thread.sleep(1000);
                }
                catch (final InterruptedException e) {
                    throw new FetcherException(e);
                }

                return "test";

            }), (() -> {

                Assert.fail();
                return "";

            }));

        try {

            final long firstStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch());
            final long firstTime = System.currentTimeMillis() - firstStartTime;

            final long secStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch());
            Assertions.assertThat(firstTime).isGreaterThan(
                secStartTime - System.currentTimeMillis());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
