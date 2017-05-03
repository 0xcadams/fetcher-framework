package com.lieuu.fetcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.lieuu.fetcher.exception.FetcherException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class GsonEndpointFetcher<T> extends CachingFetcher<T> {

    public GsonEndpointFetcher(final Fetcher<String> endpointUrlFetcher, final Class<T> clazz) {
        super(() -> {

            try {

                final HttpResponse<InputStream> response = Unirest.get(
                    endpointUrlFetcher.fetch()).asBinary();

                if (response.getStatus() == 200) {
                    return Fetchers.getGsonFetcher().fetch().fromJson(
                        new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8")),
                        clazz);
                }
                else {
                    throw new FetcherException("Response was not 200!");
                }

            }
            catch (final UnirestException
                         | JsonSyntaxException
                         | JsonIOException
                         | UnsupportedEncodingException e) {
                throw new FetcherException(e);
            }

        });
    }

}
