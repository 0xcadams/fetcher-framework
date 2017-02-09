package com.rentworthy.fetcher.response.source;

public enum UnlimitedSource implements Source {

    PRIMARY(1),
    SECONDARY(2),
    TERTIARY(3),
    QUATERNARY(4),
    QUINARY(5),
    SENARY(6),
    SEPTENARY(7),
    OCTONARY(8),
    NONARY(9),
    DENARY(10);

    private final int rank;

    UnlimitedSource(final int rank) {
        this.rank = rank;
    }

    public static UnlimitedSource valueOf(final int rank) {

        if (rank == PRIMARY.getRank()) {
            return UnlimitedSource.PRIMARY;
        }
        else if (rank == SECONDARY.getRank()) {
            return UnlimitedSource.SECONDARY;
        }
        else if (rank == TERTIARY.getRank()) {
            return UnlimitedSource.TERTIARY;
        }
        else if (rank == QUATERNARY.getRank()) {
            return UnlimitedSource.QUATERNARY;
        }
        else if (rank == QUINARY.getRank()) {
            return UnlimitedSource.QUINARY;
        }
        else if (rank == SENARY.getRank()) {
            return UnlimitedSource.SENARY;
        }
        else if (rank == SEPTENARY.getRank()) {
            return UnlimitedSource.SEPTENARY;
        }
        else if (rank == OCTONARY.getRank()) {
            return UnlimitedSource.OCTONARY;
        }
        else if (rank == NONARY.getRank()) {
            return UnlimitedSource.NONARY;
        }
        else {
            return UnlimitedSource.DENARY;
        }

    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public boolean isBefore(final Source other) {
        return this.rank < other.getRank();
    }

    @Override
    public boolean isAfter(final Source other) {
        return this.rank > other.getRank();
    }

    @Override
    public boolean isEqualTo(final Source other) {
        return this.rank == other.getRank();
    }

    @Override
    public boolean isBeforeOrEqualTo(final Source other) {
        return this.rank <= other.getRank();
    }

    @Override
    public boolean isAfterOrEqualTo(final Source other) {
        return this.rank >= other.getRank();
    }
}
