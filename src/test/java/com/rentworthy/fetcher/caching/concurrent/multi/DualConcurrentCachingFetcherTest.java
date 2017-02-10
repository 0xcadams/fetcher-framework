/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.rentworthy.fetcher.caching.concurrent.multi;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.rentworthy.fetcher.Fetcher;
import com.rentworthy.fetcher.FetcherFactory;
import com.rentworthy.fetcher.exception.FetcherException;

public class DualConcurrentCachingFetcherTest {

    @Test
    public void testDualConcurrentCachingFetcher() {

        final int numRuns = 1;

        for (int i = 0; i < numRuns; i++) {

            final int waitTime = 500;

            final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> {
                try {
                    Thread.sleep(waitTime);
                }
                catch (final Exception e) {
                    Assertions.fail(e.getMessage());
                }
                return "first";
            }, () -> "second");

            try {
                Assert.assertEquals("second", fetcher.fetch());
                Assert.assertEquals("second", fetcher.fetch());
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

                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());

                Thread.sleep(waitTime);

                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());
                Assert.assertEquals("first", fetcher.fetch());

            }
            catch (final Exception e) {
                Assert.fail();
            }

        }

    }

    @Test
    public void testDualConcurrentCachingFetcherFasterFailing() {

        final int waitTime = 500;

        final Fetcher<String> fetcher = FetcherFactory.getMultiConcurrentFetcher(() -> {
            return "second";
        }, (() -> {
            throw new FetcherException("faster");
        }));

        try {

            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());

            try {
                Thread.sleep(waitTime + 5);
            }
            catch (final Exception e) {
                Assertions.fail(e.getMessage());
            }

            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());
            Assert.assertEquals("second", fetcher.fetch());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
