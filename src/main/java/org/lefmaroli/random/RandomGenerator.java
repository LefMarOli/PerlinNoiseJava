package org.lefmaroli.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import org.lefmaroli.vector.DimensionalVector;
import org.lefmaroli.vector.Vector1D;
import org.lefmaroli.vector.VectorFactory;

public class RandomGenerator {

  public static final int NUMBER_OF_TEMPLATES = 10000;
  private static final Map<Integer, DimensionalVector[]> UNIT_VECTORS_TEMPLATES_MULTI_D =
      new HashMap<>(5);

  private final Random basicRandGenerator;
  private final Function<Integer, DimensionalVector[]> integerFunction;

  RandomGenerator() {
    this(System.currentTimeMillis());
  }

  public RandomGenerator(long seed) {
    this.basicRandGenerator = new Random(seed);
    integerFunction = dim -> {
      var v = new DimensionalVector[NUMBER_OF_TEMPLATES];
      generateMultiDSamples(basicRandGenerator, dim, v);
      return v;
    };
  }

  private static void generateMultiDSamples(
      Random randomGenerator, int dimensions, DimensionalVector[] templates) {
    var lengthLimit = 1E-4;
    var max = 1.0;
    var min = -1.0;
    for (var i = 0; i < NUMBER_OF_TEMPLATES; i++) {
      if (dimensions == 1) {
        double value = randomGenerator.nextDouble() * (max - min) + min;
        templates[i] = new Vector1D(value);
      } else {
        DimensionalVector vectorMultiD;
        double length;
        var coordinates = new double[dimensions];
        do {
          for (var j = 0; j < dimensions; j++) {
            coordinates[j] = randomGenerator.nextDouble() * (max - min + 1) + min;
          }
          vectorMultiD = VectorFactory.getVectorForCoordinates(coordinates);
          length = vectorMultiD.getLength();
        } while (length < lengthLimit);
        templates[i] = vectorMultiD.normalize();
      }
    }
  }

  public DimensionalVector getRandomUnitVectorOfDim(int dimension) {
    UNIT_VECTORS_TEMPLATES_MULTI_D.computeIfAbsent(
        dimension,
        integerFunction);
    var rand = basicRandGenerator.nextInt(NUMBER_OF_TEMPLATES);
    return UNIT_VECTORS_TEMPLATES_MULTI_D.get(dimension)[rand];
  }
}
