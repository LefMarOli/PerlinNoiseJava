package org.lefmaroli.perlin;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class PerlinNoiseTest {

  private final long randomSeed = System.currentTimeMillis();

  @ParameterizedTest(name = "{index} Dim:{0} - {2}")
  @MethodSource("invalidDimensions")
  void testInvalidDimension(int dimension, double[] coordinates, String title) {
    PerlinNoise perlinNoise = new PerlinNoise(System.currentTimeMillis());
    Assertions.assertThrows(IllegalArgumentException.class, () -> perlinNoise.getFor(coordinates));
  }

  private static Stream<Arguments> invalidDimensions() {
    return Stream.of(
        Arguments.of(0, new double[0], "empty array"), Arguments.of(6, new double[6], "too big"));
  }

  @ParameterizedTest(name = "{index} Dim:{0}")
  @MethodSource("valuesBounded")
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
}
