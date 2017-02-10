/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package com.rentworthy.fetcher;

import com.rentworthy.fetcher.response.FetcherResponse;

public interface MultiFetcher<T> extends Fetcher<FetcherResponse<T>> {

}
