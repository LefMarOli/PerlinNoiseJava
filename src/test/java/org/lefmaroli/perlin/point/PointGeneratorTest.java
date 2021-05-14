package org.lefmaroli.perlin.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;

public class PointGeneratorTest {

  private static final double noiseStepSize = 1.0 / 51;
  private static final int expectedCount = 500;
  private final long randomSeed = System.currentTimeMillis();
  private PointGenerator defaultGenerator;

  private static void assertExpectedArrayEqualsActual(Double[] expected, Double[] actual) {
    assertEquals(expected.length, actual.length, 1.0E-18);
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], actual[i], 1.0E-18);
    }
  }

  @Before
  public void setup() {
    defaultGenerator = new PointGenerator(noiseStepSize, 1.0, randomSeed);
  }

  @Test
  public void testDimension() {
    assertEquals(1, defaultGenerator.getDimensions());
  }

  @Test
  public void testValuesBounded() {
    for (int i = 0; i < 1000000; i++) {
      Double pointNoiseData = defaultGenerator.getNext();
      assertNotNull(pointNoiseData);
      assertTrue("Value is greater than 1.0:" + pointNoiseData, pointNoiseData < 1.0);
      assertTrue("Value is lower than 0.0:" + pointNoiseData, pointNoiseData > 0.0);
    }
  }

  @Test
  public void testValuesMultipliedByFactor() {
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
    assertExpectedArrayEqualsActual(values, actualAmplifiedValues);
  }

  @Test
  public void testCreateSamePoints() {
    PointGenerator sameLayer = new PointGenerator(noiseStepSize, 1.0, randomSeed);
    for (int i = 0; i < expectedCount; i++) {
      assertEquals(defaultGenerator.getNext(), sameLayer.getNext(), 0.0);
    }
  }

  @Test
  public void testCreateDifferentPointsForDifferentSeed() {
    PointGenerator sameLayer = new PointGenerator(noiseStepSize, 1.0, randomSeed + 1);
    for (int i = 0; i < expectedCount; i++) {
      Double val = defaultGenerator.getNext();
      assertNotEquals("Values are equal for i: " + i + ", value: " + val, val, sameLayer.getNext());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreate() {
    new PointGenerator(-5, 1.0, 0L);
  }

  @Test
  public void testEquals() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 1.0, 0L);
    assertEquals(layer, layer2);
  }

  @Test
  public void testNotEqualNotSameSeed() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 1.0, 1L);
    assertNotEquals(layer, layer2);
  }

  @Test
  public void testNotEqualNotSameAmplitude() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 2.0, 0L);
    assertNotEquals(layer, layer2);
  }

  @Test
  public void testNotEqualNotSameNoiseStepSize() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 6, 1.0, 0L);
    assertNotEquals(layer, layer2);
  }

  @Test
  public void testHashCode() {
    PointGenerator layer = new PointGenerator(1.0 / 5, 1.0, 0L);
    PointGenerator layer2 = new PointGenerator(1.0 / 5, 1.0, 0L);
    assertEquals(layer, layer2);
    assertEquals(layer.hashCode(), layer2.hashCode());
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(PointGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "perlin",
            "perlinData",
            "currentPosition",
            "generated",
            "containers",
            "containersCount")
        .verify();
  }

  @Test
  public void getNoiseStepSize() {
    assertEquals(noiseStepSize, defaultGenerator.getNoiseStepSize(), 1E-9);
  }
}
