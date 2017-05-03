package com.lieuu.fetcher;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.lieuu.fetcher.exception.FetcherException;

class CachingFileFetcher extends CachingFetcher<String> {

    public CachingFileFetcher(final Fetcher<String> fileNameFetcher) {
        super(() -> {

            try (final BufferedReader br = new BufferedReader(new FileReader(fileNameFetcher.fetch()))) {

                final StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }

                return sb.toString();

            }
            catch (final FileNotFoundException e) {
                throw new FetcherException(e);
            }
            catch (final IOException e) {
                throw new FetcherException(e);
            }

        });
    }

}
