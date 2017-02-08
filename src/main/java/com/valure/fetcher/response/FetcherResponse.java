package com.valure.fetcher.response;

import com.valure.fetcher.response.source.Source;

public interface FetcherResponse<T> {

    public Source source();

    public T value();

}
