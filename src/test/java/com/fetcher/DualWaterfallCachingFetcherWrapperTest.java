package com.fetcher;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.fetcher.CachingFetcherWrapper;
import com.fetcher.DualWaterfallCachingFetcherWrapper;
import com.fetcher.Fetcher;
import com.fetcher.exception.FetcherException;
import com.fetcher.response.DualFetcherResponse;

public class DualWaterfallCachingFetcherWrapperTest {

    @Test
    public void dualWaterfallCachingFetcherWrapperTest() {

        final Fetcher<DualFetcherResponse<String>> fetcher = new DualWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> "test"), new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), e -> Assert.fail());

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
    public void dualWaterfallCachingFetcherWrapperBackupTest() {

        final Fetcher<DualFetcherResponse<String>> fetcher = new DualWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {
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
    public void dualWaterfallCachingFetcherWrapperAllFailingTest() {

        final Fetcher<DualFetcherResponse<String>> fetcher = new DualWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should never reach here!");
        }), new CachingFetcherWrapper<String>(() -> {
            throw new FetcherException("should reach here!");
        }), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {
            fetcher.fetch().value();
            Assert.fail(); // should not reach this
        } catch (final FetcherException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("com.valure.fetcher.exception.FetcherException: should reach here!");
        }

    }

    @Test
    public void dualWaterfallCachingFetcherWrapperTimingTest() {

        final Fetcher<DualFetcherResponse<String>> fetcher = new DualWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {

            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                throw new FetcherException(e);
            }

            throw new FetcherException("should never reach here!");

        }), new CachingFetcherWrapper<String>(() -> "test"), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {

            final long firstStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            final long firstTime = System.currentTimeMillis() - firstStartTime;

            final long secStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            Assertions.assertThat(firstTime).isGreaterThan(secStartTime - System.currentTimeMillis());

        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void dualWaterfallCachingFetcherWrapperTimingBackupTest() {

        final Fetcher<DualFetcherResponse<String>> fetcher = new DualWaterfallCachingFetcherWrapper<String>(new CachingFetcherWrapper<String>(() -> {

            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                throw new FetcherException(e);
            }

            return "test";

        }), new CachingFetcherWrapper<String>(() -> {

            Assert.fail();
            return "";

        }), e -> Assertions.assertThat(e.getMessage()).contains("should never reach here!"));

        try {

            final long firstStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            final long firstTime = System.currentTimeMillis() - firstStartTime;

            final long secStartTime = System.currentTimeMillis();
            Assert.assertEquals("test", fetcher.fetch().value());
            Assertions.assertThat(firstTime).isGreaterThan(secStartTime - System.currentTimeMillis());

        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
