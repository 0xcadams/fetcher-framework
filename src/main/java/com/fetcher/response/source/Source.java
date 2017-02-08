/**
 * @author  cadams2
 * @since   Feb 8, 2017
 */
package com.fetcher.response.source;

public interface Source {

    public int getRank();
    
    public boolean isBefore(Source other);

    public boolean isAfter(Source other);

    public boolean isEqualTo(Source other);

    public boolean isBeforeOrEqualTo(Source other);

    public boolean isAfterOrEqualTo(Source other);

}
