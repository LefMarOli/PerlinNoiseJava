package org.lefmaroli.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.lefmaroli.vector.Vector2D;
import org.lefmaroli.vector.Vector3D;
import org.lefmaroli.vector.Vector4D;

public class RandomGenerator {

  public static final int NUMBER_OF_TEMPLATES = 10000;
  private static final Vector2D[] UNIT_VECTORS_TEMPLATES_2D = new Vector2D[NUMBER_OF_TEMPLATES];
  private static final Vector3D[] UNIT_VECTORS_TEMPLATES_3D = new Vector3D[NUMBER_OF_TEMPLATES];
  private static final Vector4D[] UNIT_VECTORS_TEMPLATES_4D = new Vector4D[NUMBER_OF_TEMPLATES];

  private static void generate2DSamples(Random randomGenerator) {
    // Generate unit 2D vectors
    double lengthLimit = 1E-4;
    double max = 1.0;
    double min = -1.0;
    for (int i = 0; i < NUMBER_OF_TEMPLATES; i++) {
      Vector2D vector2D = new Vector2D(0, 0);
      double length = 0.0;
      while (length < lengthLimit) {
        double x = randomGenerator.nextDouble() * (max - min + 1) + min;
        double y = randomGenerator.nextDouble() * (max - min + 1) + min;
        vector2D = new Vector2D(x, y);
        length = vector2D.getLength();
      }
      UNIT_VECTORS_TEMPLATES_2D[i] = vector2D.normalize();
    }
  }

  private final Random basicRandGenerator;

  RandomGenerator() {
    this(System.currentTimeMillis());
  }

  public RandomGenerator(long seed) {
    this.basicRandGenerator = new Random(seed);
  }

  private static void generate3DSamples(Random randomGenerator) {
    double lengthLimit = 1E-4;
    double max = 1.0;
    double min = -1.0;
    for (int i = 0; i < NUMBER_OF_TEMPLATES; i++) {
      Vector3D vector3D = new Vector3D(0, 0, 0);
      double length = 0.0;
      while (length < lengthLimit) {
        double x = randomGenerator.nextDouble() * (max - min + 1) + min;
        double y = randomGenerator.nextDouble() * (max - min + 1) + min;
        double z = randomGenerator.nextDouble() * (max - min + 1) + min;
        vector3D = new Vector3D(x, y, z);
        length = vector3D.getLength();
      }
      UNIT_VECTORS_TEMPLATES_3D[i] = vector3D.normalize();
    }
  }

  private static void generate4DSamples(Random randomGenerator) {
    double lengthLimit = 1E-4;
    double max = 1.0;
    double min = -1.0;
    for (int i = 0; i < NUMBER_OF_TEMPLATES; i++) {
      Vector4D vector4D = new Vector4D(0, 0, 0, 0);
      double length = 0.0;
      while (length < lengthLimit) {
        double x = randomGenerator.nextDouble() * (max - min + 1) + min;
        double y = randomGenerator.nextDouble() * (max - min + 1) + min;
        double z = randomGenerator.nextDouble() * (max - min + 1) + min;
        double t = randomGenerator.nextDouble() * (max - min + 1) + min;
        vector4D = new Vector4D(x, y, z, t);
        length = vector4D.getLength();
      }
      UNIT_VECTORS_TEMPLATES_4D[i] = vector4D.normalize();
    }
  }

  public double getRandomUnitDouble(){
    return basicRandGenerator.nextDouble();
  }

  public Vector2D getRandomUnitVector2D() {
    if (UNIT_VECTORS_TEMPLATES_2D[0] == null) {
      generate2DSamples(basicRandGenerator);
    }
    int rand = basicRandGenerator.nextInt(UNIT_VECTORS_TEMPLATES_2D.length);
    return UNIT_VECTORS_TEMPLATES_2D[rand];
  }

  public Vector3D getRandomUnitVector3D() {
    if (UNIT_VECTORS_TEMPLATES_3D[0] == null) {
      generate3DSamples(basicRandGenerator);
    }
    int rand = basicRandGenerator.nextInt(UNIT_VECTORS_TEMPLATES_3D.length);
    return UNIT_VECTORS_TEMPLATES_3D[rand];
  }

  public Vector4D getRandomUnitVector4D() {
    if(UNIT_VECTORS_TEMPLATES_4D[0] == null){
      generate4DSamples(basicRandGenerator);
    }
    int rand = basicRandGenerator.nextInt(UNIT_VECTORS_TEMPLATES_4D.length);
    return UNIT_VECTORS_TEMPLATES_4D[rand];
  }
}
