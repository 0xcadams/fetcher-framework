package com.valure.fetcher;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.valure.fetcher.CachingFetcherWrapper;
import com.valure.fetcher.Fetcher;
import com.valure.fetcher.TripleWaterfallCachingFetcherWrapper;
import com.valure.fetcher.exception.FetcherException;
import com.valure.fetcher.response.TripleFetcherResponse;

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
            Assertions.assertThat(e.getMessage()).isEqualTo("com.valure.fetcher.exception.FetcherException: should reach here!");
        }

    }

}
