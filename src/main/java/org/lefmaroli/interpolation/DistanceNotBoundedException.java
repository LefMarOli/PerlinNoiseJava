package org.lefmaroli.interpolation;

import java.util.Objects;
import org.lefmaroli.interpolation.Interpolation.Dimension;

public class DistanceNotBoundedException extends ValueNotBoundedException {

  public DistanceNotBoundedException(int dimensionIndex) {
    super(
        "Distance for dimension "
            + Objects.requireNonNull(Dimension.getFromIndex(dimensionIndex)).getName());
  }
}
