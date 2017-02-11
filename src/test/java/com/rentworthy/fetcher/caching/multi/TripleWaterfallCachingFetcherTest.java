package com.rentworthy.fetcher.caching.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.Fetchers;
import com.rentworthy.fetcher.exception.FetcherException;

public class TripleWaterfallCachingFetcherTest {

    @Test
    public void tripleWaterfallCachingFetcherTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> "test"),
            (() -> {
                throw new FetcherException("should never reach here!");
            }),
            (() -> {
                throw new FetcherException("should never reach here!");
            }));

        try {
            Assert.assertEquals("test", fetcher.fetch());
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
    public void tripleWaterfallCachingFetcherBackupTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> {
                throw new FetcherException("should never reach here!");
            }),
            (() -> "test"),
            (() -> {
                throw new FetcherException("should never reach here!");
            }));

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
    public void tripleWaterfallCachingFetcherBackupBackupTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> {
                throw new FetcherException("should never reach here!");
            }),
            (() -> {
                throw new FetcherException("should never reach here!");
            }),
            (() -> "test"));

        try {
            Assert.assertEquals("test", fetcher.fetch());
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
    public void tripleWaterfallCachingFetcherAllFailingTest() {

        final Fetcher<String> fetcher = Fetchers.getWaterfallFetcher(
            e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"),
            (() -> {
                throw new FetcherException("should never reach here!");
            }),
            (() -> {
                throw new FetcherException("should never reach here!");
            }),
            (() -> {
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

}
