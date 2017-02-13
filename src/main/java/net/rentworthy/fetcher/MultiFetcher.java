/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package net.rentworthy.fetcher;

import net.rentworthy.fetcher.response.FetcherResponse;

public interface MultiFetcher<T> extends Fetcher<FetcherResponse<T>> {

}
