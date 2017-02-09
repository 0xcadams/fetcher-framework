/**
 * @author  cadams2
 * @since   Feb 9, 2017
 */
package com.rentworthy.fetcher.response;

import com.rentworthy.fetcher.response.source.Source;
import com.rentworthy.fetcher.response.source.UnlimitedSource;

public class FetcherResponseFactory<S> {

    public static <S> FetcherResponse<S> getFetcherResponse(final int rankFinal, final S value) {

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

//    public static <S> FetcherResponse<S> getSingleFetcherResponse(final S value) {
//
//        return new FetcherResponse<S>() {
//
//            @Override
//            public Source source() throws SingleFetcherException {
//                throw new SingleFetcherException("Single fetcher response!");
//            }
//
//            @Override
//            public S value() {
//                return value;
//            }
//
//        };
//
//    }

}
