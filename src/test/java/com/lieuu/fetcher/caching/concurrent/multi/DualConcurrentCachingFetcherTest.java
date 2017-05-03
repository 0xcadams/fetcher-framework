/**
 * @author cadams2
 * @since Feb 7, 2017
 */
package com.lieuu.fetcher.caching.concurrent.multi;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;

public class DualConcurrentCachingFetcherTest {

    @Test
    public void testDualConcurrentCachingFetcher() {

        final int numRuns = 1;

        for (int i = 0; i < numRuns; i++) {

            final int waitTime = 500;

            final Fetcher<String> fetcher = Fetchers.getMultiConcurrentFetcher(() -> {
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

        final Fetcher<String> fetcher = Fetchers.getMultiConcurrentFetcher(() -> {
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

    @Test
    public void testDualConcurrentCachingFetcherFailing() {

        final Fetcher<String> fetcher = Fetchers.getMultiConcurrentFetcher(() -> {
            try {
                Thread.sleep(1000);
                throw new IOException("Networking unavailable.");
            }
            catch (final InterruptedException | IOException e) {
                throw new FetcherException(e);
            }
        }, () -> {
            return "backup value";
        });

        try {

            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());

            try {
                Thread.sleep(1100);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }

            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    // @Test
    public void testDualConcurrentCachingFetcherFailingThenPassing() {

        final int count = 0;

        final Fetcher<String> fetcher = Fetchers.getExpiringMultiConcurrentFetcher(2000, () -> {
            try {
                System.out.println("running");
                Thread.sleep(800);
                throw new IOException();
            }
            catch (final InterruptedException | IOException e) {
                throw new FetcherException(e);
            }
        }, () -> {
            return "backup value";
        });

        try {

            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());

            try {
                Thread.sleep(6000);
            }
            catch (final InterruptedException e) {
                e.printStackTrace();
            }

            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());
            Assert.assertEquals("backup value", fetcher.fetch());

        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
