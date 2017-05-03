package com.lieuu.fetcher;

import com.google.gson.Gson;

public class GsonCachingFetcher extends CachingFetcher<Gson> {

    public GsonCachingFetcher() {
        super(() -> new Gson());
    }

}
