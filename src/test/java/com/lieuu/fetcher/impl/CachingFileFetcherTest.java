package com.lieuu.fetcher.impl;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;

public class CachingFileFetcherTest {

    @Test
    public void cachingFileFetcherTest() {

        final Fetcher<String> fetcher = Fetchers.Implementations.getCachingFileFetcher(
            () -> "src/test/resources/demo.txt");

        try {
            Assertions.assertThat(fetcher.fetch()).isEqualTo("this is demo text");
        }
        catch (final FetcherException e) {
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void cachingFileFetcherTest2() {

        final Fetcher<String> fetcher = Fetchers.Implementations.getCachingFileFetcher(
            () -> "src/test/resources/demo2.txt");

        try {
            Assertions.assertThat(fetcher.fetch()).isEqualTo("textlsdfsdf");
        }
        catch (final FetcherException e) {
            Assert.fail(e.getMessage());
        }

    }

}
