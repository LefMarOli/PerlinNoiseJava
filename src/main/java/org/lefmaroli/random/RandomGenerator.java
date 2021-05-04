package org.lefmaroli.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.lefmaroli.vector.VectorMultiD;

public class RandomGenerator {

  public static final int NUMBER_OF_TEMPLATES = 10000;
  private static final Map<Integer, VectorMultiD[]> UNIT_VECTORS_TEMPLATES_MULTI_D =
      new HashMap<>(5);

  private final Random basicRandGenerator;

  RandomGenerator() {
    this(System.currentTimeMillis());
  }

  public RandomGenerator(long seed) {
    this.basicRandGenerator = new Random(seed);
  }

  private static void generateMultiDSamples(
      Random randomGenerator, int dimensions, VectorMultiD[] templates) {
    var lengthLimit = 1E-4;
    var max = 1.0;
    var min = -1.0;
    for (var i = 0; i < NUMBER_OF_TEMPLATES; i++) {
      var vectorMultiD = new VectorMultiD(0);
      if (dimensions == 1) {
        double value = randomGenerator.nextDouble() * (max - min + 1) + min;
        templates[i] = new VectorMultiD(value);
      } else {
        var length = 0.0;
        var coordinates = new double[dimensions];
        while (length < lengthLimit) {
          for (var j = 0; j < dimensions; j++) {
            coordinates[j] = randomGenerator.nextDouble() * (max - min + 1) + min;
          }
          vectorMultiD = new VectorMultiD(coordinates);
          length = vectorMultiD.getLength();
        }
        templates[i] = vectorMultiD.normalize();
      }
    }
  }

  public VectorMultiD getRandomUnitVectorOfDim(int dimension) {
    UNIT_VECTORS_TEMPLATES_MULTI_D.computeIfAbsent(dimension, k-> {
      var v = new VectorMultiD[NUMBER_OF_TEMPLATES];
      generateMultiDSamples(
          basicRandGenerator, dimension, v);
      return v;
    });
    var rand = basicRandGenerator.nextInt(NUMBER_OF_TEMPLATES);
    return UNIT_VECTORS_TEMPLATES_MULTI_D.get(dimension)[rand];
  }
}
