package org.lefmaroli.perlin.bounds;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BoundGridTest {

  @ParameterizedTest
  @MethodSource("invalidDimensions")
  void testInvalidDimensionCreate(int dimension) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> BoundGrid.getNewBoundGridForDimension(dimension, 2));
  }

  private static Stream<Arguments> invalidDimensions() {
    return Stream.of(Arguments.of(-1), Arguments.of(0), Arguments.of(6));
  }

  @ParameterizedTest
  @MethodSource("invalidNumberOfBounds")
  void testInvalidNumberOfBoundsCreate(int numberOfBounds) {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> BoundGrid.getNewBoundGridForDimension(1, numberOfBounds));
  }

  private static Stream<Arguments> invalidNumberOfBounds() {
    return Stream.of(Arguments.of(-1), Arguments.of(0), Arguments.of(6));
  }
}
