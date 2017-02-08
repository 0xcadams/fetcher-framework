/**
 * @author  cadams2
 * @since   Feb 8, 2017
 */
package com.fetcher.response.source;

public interface Source {

    public boolean isBefore(QuadrupleSource other);

    public boolean isAfter(QuadrupleSource other);

    public boolean isEqualTo(QuadrupleSource other);

    public boolean isBeforeOrEqualTo(QuadrupleSource other);

    public boolean isAfterOrEqualTo(QuadrupleSource other);

}
