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
    double lengthLimit = 1E-4;
    double max = 1.0;
    double min = -1.0;
    for (int i = 0; i < NUMBER_OF_TEMPLATES; i++) {
      VectorMultiD vectorMultiD = new VectorMultiD(0);
      if (dimensions == 1) {
        double value = randomGenerator.nextDouble() * (max - min + 1) + min;
        templates[i] = new VectorMultiD(value);
      } else {
        double length = 0.0;
        double[] coordinates = new double[dimensions];
        while (length < lengthLimit) {
          for (int j = 0; j < dimensions; j++) {
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
    if (!UNIT_VECTORS_TEMPLATES_MULTI_D.containsKey(dimension)) {
      UNIT_VECTORS_TEMPLATES_MULTI_D.put(dimension, new VectorMultiD[NUMBER_OF_TEMPLATES]);
      generateMultiDSamples(
          basicRandGenerator, dimension, UNIT_VECTORS_TEMPLATES_MULTI_D.get(dimension));
    }
    int rand = basicRandGenerator.nextInt(NUMBER_OF_TEMPLATES);
    return UNIT_VECTORS_TEMPLATES_MULTI_D.get(dimension)[rand];
  }
}
