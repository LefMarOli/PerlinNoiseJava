package org.lefmaroli.perlin.point;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PointGeneratorTest {

  private static final double noiseStepSize = 1.0 / 51;
  private static final int expectedCount = 500;
  private final long randomSeed = System.currentTimeMillis();
  private PointGenerator defaultGenerator;

  @BeforeEach
  void setup() {
    defaultGenerator = new PointGenerator(noiseStepSize, 1.0, randomSeed);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(1, defaultGenerator.getDimensions());
  }

  @Test
  void testValuesBounded() {
    for (int i = 0; i < 1000000; i++) {
      Double pointNoiseData = defaultGenerator.getNext();
      Assertions.assertNotNull(pointNoiseData);
      Assertions.assertTrue(pointNoiseData < 1.0, "Value is greater than 1.0:" + pointNoiseData);
      Assertions.assertTrue(pointNoiseData > 0.0, "Value is lower than 0.0:" + pointNoiseData);
    }
  }

  @Test
  void testValuesMultipliedByFactor() {
    Random random = new Random(System.currentTimeMillis());
    double amplitudeFactor = random.nextDouble() * 100;
    PointGenerator amplifiedLayer = new PointGenerator(noiseStepSize, amplitudeFactor, randomSeed);

    Double[] values = new Double[expectedCount];
    Double[] actualAmplifiedValues = new Double[expectedCount];
    for (int i = 0; i < expectedCount; i++) {
      values[i] = defaultGenerator.getNext();
      actualAmplifiedValues[i] = amplifiedLayer.getNext();
    }

    for (int i = 0; i < values.length; i++) {
      values[i] = values[i] * amplitudeFactor;
    }
    Assertions.assertArrayEquals(values, actualAmplifiedValues);
  }

  @Test
  void testCreateSamePoints() {
    PointGenerator sameLayer = new PointGenerator(noiseStepSize, 1.0, randomSeed);
    for (int i = 0; i < expectedCount; i++) {
      Assertions.assertEquals(defaultGenerator.getNext(), sameLayer.getNext(), 0.0);
    }
  }

  @Test
  void testCreateDifferentPointsForDifferentSeed() {
    PointGenerator sameLayer = new PointGenerator(noiseStepSize, 1.0, randomSeed + 1);
    for (int i = 0; i < expectedCount; i++) {
      Double val = defaultGenerator.getNext();
      Assertions.assertNotEquals(val, sameLayer.getNext(),
          "Values are equal for i: " + i + ", value: " + val);
    }
  }

  @Test
  void testCreate() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> new PointGenerator(-5, 1.0, 0L));
  }

  @Test
  void testEquals() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 1.0, 0L);
    Assertions.assertEquals(layer, layer2);
  }

  @Test
  void testNotEqualNotSameSeed() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 1.0, 1L);
    Assertions.assertNotEquals(layer, layer2);
  }

  @Test
  void testNotEqualNotSameAmplitude() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 2.0, 0L);
    Assertions.assertNotEquals(layer, layer2);
  }

  @Test
  void testNotEqualNotSameNoiseStepSize() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 6, 1.0, 0L);
    Assertions.assertNotEquals(layer, layer2);
  }

  @Test
  void testHashCode() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 1.0, 0L);
    Assertions.assertEquals(layer, layer2);
    Assertions.assertEquals(layer.hashCode(), layer2.hashCode());
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(PointGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "perlin", "perlinData", "currentPosition", "generated", "containers", "containersCount")
        .verify();
  }

  @Test
  void getNoiseStepSize() {
    Assertions.assertEquals(noiseStepSize, defaultGenerator.getNoiseStepSize(), 1E-9);
  }
}
