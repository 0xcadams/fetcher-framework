package com.lieuu.fetcher.impl;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import com.lieuu.fetcher.Fetcher;
import com.lieuu.fetcher.Fetchers;
import com.lieuu.fetcher.exception.FetcherException;
import com.lieuu.fetcher.impl.demo.DemoObject;

public class GsonEndpointFetcherTest {

    @Test
    public void gsonEndpointFetcherTest() {

        final Fetcher<DemoObject> fetcher = Fetchers.Implementations.getGsonEndpointFetcher(
            () -> "https://s3-us-west-2.amazonaws.com/lieuu-fetcher-framework/demo_json.json", DemoObject.class);

        try {
            Assertions.assertThat(fetcher.fetch().getId()).isEqualTo(1);
            Assertions.assertThat(fetcher.fetch().getUserId()).isEqualTo(1);
            Assertions.assertThat(fetcher.fetch().getTitle()).startsWith("sunt aut");
            Assertions.assertThat(fetcher.fetch().getBody()).startsWith("quia et suscipit");
        }
        catch (final FetcherException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

}
