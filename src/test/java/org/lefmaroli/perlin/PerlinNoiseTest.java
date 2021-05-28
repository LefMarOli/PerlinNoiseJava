package org.lefmaroli.perlin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainer;
import org.lefmaroli.perlin.PerlinNoise.PerlinNoiseDataContainerBuilder;

class PerlinNoiseTest {

  private final long randomSeed = System.currentTimeMillis();

  @ParameterizedTest(name = "{index} Dim:{0} - {2}")
  @MethodSource("invalidDimensions")
  @SuppressWarnings("unused")
  void testInvalidDimension(int dimension, double[] coordinates, String title) {
    PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
    Assertions.assertThrows(IllegalArgumentException.class, () -> perlinNoise.getFor(coordinates));
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidDimensions() {
    return Stream.of(
        Arguments.of(0, new double[0], "empty array"), Arguments.of(6, new double[6], "too big"));
  }

  @ParameterizedTest(name = "{index} Dim:{0}")
  @MethodSource("valuesBounded")
  @SuppressWarnings("unused")
  void testValuesBounded(int dimension, double[] args) {
    PerlinNoise perlinNoise = new PerlinNoise(randomSeed);
    int numIterations = 100000;
    double value;
    for (int i = 0; i < numIterations; i++) {
      for (int j = 0; j < args.length; j++) {
        args[j] *= i;
      }
      value = perlinNoise.getFor(args);
      Assertions.assertTrue(value >= 0.0);
      Assertions.assertTrue(value <= 1.0);
    }
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> valuesBounded() {
    return Stream.of(
        Arguments.of(1, new double[] {0.5}),
        Arguments.of(2, new double[] {0.5, 0.3}),
        Arguments.of(3, new double[] {0.5, 0.3, 0.5472}),
        Arguments.of(4, new double[] {0.5, 0.3, 0.5472, 0.587}));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, 6})
  void testInvalidDimensionPerlinNoiseContainerBuilder(int dimension) {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new PerlinNoise.PerlinNoiseDataContainerBuilder(dimension, 0L));
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testEqualsContainersParams")
  @SuppressWarnings("unused")
  void testEqualsContainers(Object first, Object second, String title) {
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testEqualsContainersParams() {
    var builder = new PerlinNoiseDataContainerBuilder(2, 0L);
    var otherBuilder = new PerlinNoiseDataContainerBuilder(2, 0L);
    PerlinNoiseDataContainer first = builder.createNewContainer();
    return Stream.of(
        Arguments.of(first, first, "test equal to itself"),
        Arguments.of(
            first, builder.createNewContainer(), "test equal to other container from same builder"),
        Arguments.of(
            first,
            otherBuilder.createNewContainer(),
            "test equal to other container from equivalent builder"));
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testNotEqualsContainersParams")
  @SuppressWarnings("unused")
  void testNotEqualsContainers(Object first, Object second, String title) {
    assertNotEquals(first, second);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testNotEqualsContainersParams() {
    long randomSeed = 0L;
    int dimension = 2;
    var builder = new PerlinNoiseDataContainerBuilder(dimension, randomSeed);
    var otherBuilder = new PerlinNoiseDataContainerBuilder(dimension + 1, randomSeed);
    var thirdBuilder = new PerlinNoiseDataContainerBuilder(dimension, randomSeed + 1);
    PerlinNoiseDataContainer first = builder.createNewContainer();
    return Stream.of(
        Arguments.of(first, null, "test not equal to null"),
        Arguments.of(
            first, new Random(), "test not equal to other class"),
        Arguments.of(
            first,
            otherBuilder.createNewContainer(),
            "test not equal to other container from builder of different dimension"),
        Arguments.of(
            first,
            thirdBuilder.createNewContainer(),
            "test not equal to other container from builder of different seed"));
  }
}
