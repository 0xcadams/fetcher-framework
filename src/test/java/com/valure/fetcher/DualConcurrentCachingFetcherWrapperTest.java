/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.valure.fetcher;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.valure.fetcher.exception.FetcherException;
import com.valure.fetcher.response.DualFetcherResponse;

public class DualConcurrentCachingFetcherWrapperTest {

    @Test
    public void testSubstitutingCachingFetcherWrapper() {

        final int numRuns = 1;

        for (int i = 0; i < numRuns; i++) {

            final int waitTime = 500;

            final Fetcher<DualFetcherResponse<String>> fetcher = new DualConcurrentCachingFetcherWrapper<String>(
                    new ConcurrentCachingFetcherWrapper<String>(new ConcurrentFetcherWrapper<String>(() -> {
                        try {
                            Thread.sleep(waitTime);
                        } catch (final Exception e) {
                            Assertions.fail(e.getMessage());
                        }
                        return "first";
                    })), new ConcurrentCachingFetcherWrapper<String>(new ConcurrentFetcherWrapper<String>(() -> {
                        return "second";
                    })));

            try {
                Assert.assertEquals("second", fetcher.fetch().value());
                Assert.assertEquals("second", fetcher.fetch().value());
            } catch (final FetcherException e) {
                Assert.fail();
            }

            try {
                Thread.sleep(waitTime*3);
            } catch (final Exception e) {
                Assertions.fail(e.getMessage());
            }

            try {

                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());

                Thread.sleep(waitTime);

                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());
                Assert.assertEquals("first", fetcher.fetch().value());

            } catch (final Exception e) {
                Assert.fail();
            }

        }

    }

    @Test
    public void testSubstitutingCachingFetcherWrapperFasterFailing() {

        final int waitTime = 500;

        final Fetcher<DualFetcherResponse<String>> fetcher = new DualConcurrentCachingFetcherWrapper<String>(new ConcurrentCachingFetcherWrapper<String>(new ConcurrentFetcherWrapper<String>(() -> {
            return "second";
        })), new ConcurrentCachingFetcherWrapper<String>(new ConcurrentFetcherWrapper<String>(() -> {
            throw new FetcherException("faster");
        })));

        try {

            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());

            try {
                Thread.sleep(waitTime + 5);
            } catch (final Exception e) {
                Assertions.fail(e.getMessage());
            }

            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());
            Assert.assertEquals("second", fetcher.fetch().value());

        } catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
