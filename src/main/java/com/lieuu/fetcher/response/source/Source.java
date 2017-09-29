package com.lieuu.fetcher.response.source;

public enum Source {

  FIRST(1), SECOND(2), THIRD(3), FOURTH(4), FIFTH(5), SIXTH(6), SEVENTH(7), EIGHTH(8), NINTH(
    9), TENTH(10), ELEVENTH(11), TWELFTH(12), THIRTEENTH(13), FOURTEENTH(14), FIFTEENTH(
    15), SIXTEENTH(16), SEVENTEENTH(17), EIGHTEENTH(18), NINETEENTH(19), TWENTIETH(20), UNKNOWN(-1);

  private final int rank;

  Source(final int rank) {
    this.rank = rank;
  }

  public final static Source valueOf(final int rank) {

    for (final Source src : Source.values()) {
      if (rank == src.getRank()) {
        return src;
      }
    }

    return UNKNOWN;

  }

  public final int getRank() {
    return this.rank;
  }

  public final boolean isBefore(final Source other) {
    return this.rank < other.getRank();
  }

  public final boolean isAfter(final Source other) {
    return this.rank > other.getRank();
  }

  public final boolean isEqualTo(final Source other) {
    return this.rank == other.getRank();
  }

  public final boolean isBeforeOrEqualTo(final Source other) {
    return this.rank <= other.getRank();
  }

  public final boolean isAfterOrEqualTo(final Source other) {
    return this.rank >= other.getRank();
  }

}
