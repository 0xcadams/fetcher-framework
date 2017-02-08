package com.fetcher.response.source;

public enum TripleSource implements Source {

    PRIMARY(1), SECONDARY(2), TERTIARY(3);

    private final int rank;

    private TripleSource(final int rank) {
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
