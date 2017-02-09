package com.rentworthy.fetcher;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.FetcherResponse;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public class TripleWaterfallCachingFetcherWrapperTest {

    @Test
    public void tripleWaterfallCachingFetcherWrapperTest() {

        final Fetcher<FetcherResponse<String>> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains("should never reach here!"),
                                                                                                             new CachingFetcherWrapper<String>(() -> "test"),
                                                                                                             new CachingFetcherWrapper<String>(() -> {
                                                                                                                 throw new FetcherException("should never reach here!");
                                                                                                             }),
                                                                                                             new CachingFetcherWrapper<String>(() -> {
                                                                                                                 throw new FetcherException("should never reach here!");
                                                                                                             }));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(UnlimitedSource.PRIMARY));
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherWrapperBackupTest() {

        final Fetcher<FetcherResponse<String>> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"),
                                                                                                             new CachingFetcherWrapper<String>(() -> {
                                                                                                                 throw new FetcherException("should never reach here!");
                                                                                                             }),
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
            Assert.assertEquals(true,
                fetcher.fetch().source().isEqualTo(UnlimitedSource.SECONDARY));
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherWrapperBackupBackupTest() {

        final Fetcher<FetcherResponse<String>> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> "test"));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(UnlimitedSource.TERTIARY));
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherWrapperAllFailingTest() {

        final Fetcher<FetcherResponse<String>> fetcher = new WaterfallCachingFetcherWrapper<String>(e -> Assertions.assertThat(
            e.getMessage()).contains(
                "should never reach here!"), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> {
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
