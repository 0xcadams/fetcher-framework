package com.rentworthy.fetcher;

interface Builder<T> {

    Fetcher<T> build(Fetcher<T>... fetchers);

}
