package org.lefmaroli.perlin.bounds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BoundGridFactoryTest {

  @ParameterizedTest
  @MethodSource("invalidDimensions")
  void testInvalidDimensionCreate(int dimension) {
    assertThrows(
        IllegalArgumentException.class,
        () -> BoundGridFactory.getNewBoundGridForDimension(dimension, 2));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidDimensions() {
    return Stream.of(Arguments.of(-1), Arguments.of(0), Arguments.of(6));
  }

  @ParameterizedTest
  @MethodSource("invalidNumberOfBounds")
  void testInvalidNumberOfBoundsCreate(int numberOfBounds) {
    assertThrows(
        IllegalArgumentException.class,
        () -> BoundGridFactory.getNewBoundGridForDimension(1, numberOfBounds));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidNumberOfBounds() {
    return Stream.of(Arguments.of(-1), Arguments.of(0), Arguments.of(6));
  }

  @ParameterizedTest(name = "{index} - {3}")
  @MethodSource("invalidCoordinates")
  @SuppressWarnings("unused")
  void testInvalidCoordinatesDimensions(
      int dimension, int[] coordinates, int[] boundsIndices, String title) {
    BoundGrid boundGrid = BoundGridFactory.getNewBoundGridForDimension(dimension, 8);
    assertThrows(
        IllegalArgumentException.class,
        () -> boundGrid.getBoundForCoordinates(coordinates, boundsIndices));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidCoordinates() {
    return Stream.of(
        Arguments.of(1, new int[] {}, new int[] {1}, "Dimension 1, no coordinates"),
        Arguments.of(1, new int[] {1, 2}, new int[] {1}, "Dimension 1, two coordinates"),
        Arguments.of(2, new int[] {1}, new int[] {1, 2}, "Dimension 2, one coordinates"),
        Arguments.of(2, new int[] {1, 2, 3}, new int[] {1, 2}, "Dimension 2, three coordinates"),
        Arguments.of(3, new int[] {1, 2}, new int[] {1, 2, 3}, "Dimension 3, two coordinates"),
        Arguments.of(
            3, new int[] {1, 2, 3, 4}, new int[] {1, 2, 3}, "Dimension 3, four coordinates"),
        Arguments.of(
            4, new int[] {1, 2, 3}, new int[] {1, 2, 3, 4}, "Dimension 4, three coordinates"),
        Arguments.of(
            4, new int[] {1, 2, 3, 4, 5}, new int[] {1, 2, 3, 4}, "Dimension 4, five coordinates"),
        Arguments.of(
            5, new int[] {1, 2, 3, 4}, new int[] {1, 2, 3, 4, 5}, "Dimension 5, four coordinates"),
        Arguments.of(
            5,
            new int[] {1, 2, 3, 4, 5, 6},
            new int[] {1, 2, 3, 4, 5},
            "Dimension 5, six coordinates"));
  }

  @ParameterizedTest(name = "{index} - {3}")
  @MethodSource("invalidBoundIndices")
  @SuppressWarnings("unused")
  void testInvalidBoundIndicesDimensions(
      int dimension, int[] coordinates, int[] boundsIndices, String title) {
    BoundGrid boundGrid = BoundGridFactory.getNewBoundGridForDimension(dimension, 8);
    assertThrows(
        IllegalArgumentException.class,
        () -> boundGrid.getBoundForCoordinates(coordinates, boundsIndices));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidBoundIndices() {
    return Stream.of(
        Arguments.of(1, new int[] {1}, new int[] {}, "Dimension 1, no bound indices"),
        Arguments.of(1, new int[] {1}, new int[] {1, 2}, "Dimension 1, two bound indices"),
        Arguments.of(2, new int[] {1, 2}, new int[] {1}, "Dimension 2, one bound indices"),
        Arguments.of(2, new int[] {1, 2}, new int[] {1, 2, 3}, "Dimension 2, three bound indices"),
        Arguments.of(3, new int[] {1, 2, 3}, new int[] {1, 2}, "Dimension 3, two bound indices"),
        Arguments.of(
            3, new int[] {1, 2, 3}, new int[] {1, 2, 3, 4}, "Dimension 3, four bound indices"),
        Arguments.of(
            4, new int[] {1, 2, 3, 4}, new int[] {1, 2, 3}, "Dimension 4, three bound indices"),
        Arguments.of(
            4,
            new int[] {1, 2, 3, 4},
            new int[] {1, 2, 3, 4, 5},
            "Dimension 4, five bound indices"),
        Arguments.of(
            5,
            new int[] {1, 2, 3, 4, 5},
            new int[] {1, 2, 3, 4},
            "Dimension 5, four bound indices"),
        Arguments.of(
            5,
            new int[] {1, 2, 3, 4, 5},
            new int[] {1, 2, 3, 4, 5, 6},
            "Dimension 5, six bound indices"));
  }

  @ParameterizedTest
  @MethodSource("wrapIndices")
  void testWrapIndices(int index, int expected) {
    assertEquals(expected, BoundGrid.wrapIndexToBounds(index, 8));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> wrapIndices() {
    return Stream.of(
        Arguments.of(1, 1),
        Arguments.of(7, 7),
        Arguments.of(8, 0),
        Arguments.of(9, 1),
        Arguments.of(17, 1));
  }
}
