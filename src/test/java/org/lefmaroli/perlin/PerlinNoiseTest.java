package org.lefmaroli.perlin;

import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PerlinNoiseTest {

  private final long randomSeed = System.currentTimeMillis();

  @ParameterizedTest(name = "{index} Dim:{0} - {1}")
  @MethodSource("invalidDimensions")
  void testInvalidDimension(int dimension, String title) {
    long seed = System.currentTimeMillis();
    Assertions.assertThrows(IllegalArgumentException.class, () -> new PerlinNoise(dimension, seed));
  }

  private static Stream<Arguments> invalidDimensions() {
    return Stream.of(
        Arguments.of(-1, "negative"), Arguments.of(0, "too little"), Arguments.of(6, "too big"));
  }

  @ParameterizedTest(name = "{index} {0} - {1}")
  @MethodSource("wrongIndices")
  void testWrongNumberOfIndices(double[] args, String title) {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    Assertions.assertThrows(IllegalArgumentException.class, () -> perlinNoise.getFor(args));
  }

  private static Stream<Arguments> wrongIndices() {
    return Stream.of(
        Arguments.of(new double[] {0.5}, "too few"),
        Arguments.of(new double[] {0.5, 0.3, 15.4}, "too many"));
  }

  @ParameterizedTest(name = "{index} Dim:{0}")
  @MethodSource("valuesBounded")
  void testValuesBounded(int dimension, double[] args) {
    PerlinNoise perlinNoise = new PerlinNoise(dimension, randomSeed);
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
}
