package com.lieuu.fetcher;

import com.lieuu.fetcher.exception.FetcherException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class GsonEndpointFetcher<T> extends CachingFetcher<T> {

    public GsonEndpointFetcher(final Fetcher<String> endpointUrlFetcher, final Class<T> clazz) {
        super(() -> {

            try {

                final HttpResponse<String> response = Unirest.get(
                    endpointUrlFetcher.fetch()).asString();

                if (response.getStatus() == 200) {
                    return Fetchers.getGsonFetcher().fetch().fromJson(response.getBody(), clazz);
                }
                else {
                    throw new FetcherException("Response was not 200!");
                }

            }
            catch (final UnirestException e) {
                throw new FetcherException(e);
            }

        });
    }

}
