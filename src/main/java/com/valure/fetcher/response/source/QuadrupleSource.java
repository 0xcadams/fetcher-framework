/**
 * @author  cadams2
 * @since   Feb 8, 2017
 */
package com.valure.fetcher.response.source;

public enum QuadrupleSource implements Source {

    PRIMARY(1), SECONDARY(2), TERTIARY(3), QUATERNARY(4);

    private final int rank;

    private QuadrupleSource(final int rank) {
        this.rank = rank;
    }

    protected int getRank() {
        return this.rank;
    }

    @Override
    public boolean isBefore(final QuadrupleSource other) {
        return this.rank < other.getRank();
    }

    @Override
    public boolean isAfter(final QuadrupleSource other) {
        return this.rank > other.getRank();
    }

    @Override
    public boolean isEqualTo(final QuadrupleSource other) {
        return this.rank == other.getRank();
    }

    @Override
    public boolean isBeforeOrEqualTo(final QuadrupleSource other) {
        return this.rank <= other.getRank();
    }

    @Override
    public boolean isAfterOrEqualTo(final QuadrupleSource other) {
        return this.rank >= other.getRank();
    }

}