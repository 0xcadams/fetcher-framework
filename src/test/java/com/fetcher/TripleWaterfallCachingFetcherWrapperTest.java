package com.fetcher;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.fetcher.exception.FetcherException;
import com.fetcher.response.TripleFetcherResponse;
import com.fetcher.response.source.QuadrupleSource;
import com.fetcher.response.source.TripleSource;

public class TripleWaterfallCachingFetcherWrapperTest {

    @Test
    public void tripleWaterfallCachingFetcherWrapperTest() {

        final Fetcher<TripleFetcherResponse<String>> fetcher = new TripleWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> "test"),
                new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), new CachingFetcherWrapper<String>(() -> {
                    throw new FetcherException("should never reach here!");
                }), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(TripleSource.PRIMARY));
        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherWrapperBackupTest() {

        final Fetcher<TripleFetcherResponse<String>> fetcher = new TripleWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), new CachingFetcherWrapper<String>(() -> "test"), new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(TripleSource.SECONDARY));
        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherWrapperBackupBackupTest() {

        final Fetcher<TripleFetcherResponse<String>> fetcher = new TripleWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), new CachingFetcherWrapper<String>(() -> "test"), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals("test", fetcher.fetch().value());
            Assert.assertEquals(true, fetcher.fetch().source().isEqualTo(TripleSource.TERTIARY));
        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void tripleWaterfallCachingFetcherWrapperAllFailingTest() {

        final Fetcher<TripleFetcherResponse<String>> fetcher = new TripleWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should reach here!");
        }), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {
            fetcher.fetch();
            Assert.fail(); // should not reach this
        } catch (final FetcherException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("com.fetcher.exception.FetcherException: should reach here!");
        }

    }

}
