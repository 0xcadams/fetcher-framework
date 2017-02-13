/**
 * @author cadams2
 * @since Feb 9, 2017
 */
package net.rentworthy.fetcher;

import net.rentworthy.fetcher.response.FetcherResponse;
import net.rentworthy.fetcher.response.source.Source;
import net.rentworthy.fetcher.response.source.UnlimitedSource;

class FetcherResponseFactory<S> {

    protected static <S> FetcherResponse<S> getFetcherResponse(final int rankFinal, final S value) {

        return new FetcherResponse<S>() {

            @Override
            public Source source() {
                return UnlimitedSource.valueOf(rankFinal);
            }

            @Override
            public S value() {
                return value;
            }

        };

    }

}
