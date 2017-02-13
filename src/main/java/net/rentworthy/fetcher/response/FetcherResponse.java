package net.rentworthy.fetcher.response;

import net.rentworthy.fetcher.response.source.Source;

public interface FetcherResponse<T> {

    public Source source();

    public T value();

}
