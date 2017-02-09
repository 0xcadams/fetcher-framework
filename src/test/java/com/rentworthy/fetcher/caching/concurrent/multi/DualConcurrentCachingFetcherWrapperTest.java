/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.rentworthy.fetcher.caching.concurrent.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.MultiFetcher;
import com.rentworthy.fetcher.caching.concurrent.NonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.caching.concurrent.multi.CachingNonBlockingConcurrentFetcherWrapper;
import com.rentworthy.fetcher.exception.FetcherException;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public class DualConcurrentCachingFetcherWrapperTest {

    @Test
    public void testDualConcurrentCachingFetcherWrapper() {

        final int numRuns = 1;

        for (int i = 0; i < numRuns; i++) {

            final int waitTime = 500;

            final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<String>(new NonBlockingConcurrentFetcherWrapper<String>(() -> {
                try {
                    Thread.sleep(waitTime);
                }
                catch (final Exception e) {
                    Assertions.fail(e.getMessage());
                }
                return "first";
            }), new NonBlockingConcurrentFetcherWrapper<String>(() -> {
                return "second";
            }));

            try {
                Assert.assertEquals("second", fetcher.fetch().value());
                Assert.assertEquals("second", fetcher.fetch().value());
                Assert.assertEquals(UnlimitedSource.SECOND, fetcher.fetch().source());
            }
            catch (final FetcherException e) {
                Assert.fail();
            }

            try {
                Thread.sleep(waitTime * 3);
            }
            catch (final Exception e) {
                Assertions.fail(e.getMessage());
            }

            try {

                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals(UnlimitedSource.FIRST, fetcher.fetch().source());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());

                Thread.sleep(waitTime);

                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals(UnlimitedSource.FIRST, fetcher.fetch().source());
                Assert.assertEquals("first", fetcher.fetch().value());

            }
            catch (final Exception e) {
                Assert.fail();
            }

        }

    }

    @Test
    public void testDualConcurrentCachingFetcherWrapperFasterFailing() {

        final int waitTime = 500;

        final MultiFetcher<String> fetcher = new CachingNonBlockingConcurrentFetcherWrapper<String>(new NonBlockingConcurrentFetcherWrapper<String>(() -> {
            return "second";
        }), new NonBlockingConcurrentFetcherWrapper<String>(() -> {
            throw new FetcherException("faster");
        }));

        try {

            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());

            try {
                Thread.sleep(waitTime + 5);
            }
            catch (final Exception e) {
                Assertions.fail(e.getMessage());
            }

            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
