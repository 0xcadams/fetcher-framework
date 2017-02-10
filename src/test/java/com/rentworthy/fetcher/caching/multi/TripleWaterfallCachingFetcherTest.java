package com.rentworthy.fetcher.caching.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.MultiFetcher;
import com.rentworthy.fetcher.caching.CachingFetcher;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public class TripleWaterfallCachingFetcherTest {

    @Test
    public void tripleWaterfallCachingFetcherTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains("should never reach here!"),
                                                                                        new CachingFetcher<String>(() -> "test"),
                                                                                        new CachingFetcher<String>(() -> {
                                                                                            throw new FetcherException("should never reach here!");
                                                                                        }),
                                                                                        new CachingFetcher<String>(() -> {
                                                                                            throw new FetcherException("should never reach here!");
                                                                                        }));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(UnlimitedSource.FIRST));
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherBackupTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"),
                                                                                        new CachingFetcher<String>(() -> {
                                                                                            throw new FetcherException("should never reach here!");
                                                                                        }),
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
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(UnlimitedSource.SECOND));
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherBackupBackupTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcher<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcher<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcher<String>(() -> "test"));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(UnlimitedSource.THIRD));
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherAllFailingTest() {

        final MultiFetcher<String> fetcher = new WaterfallCachingFetcher<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcher<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcher<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcher<String>(() -> {
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
