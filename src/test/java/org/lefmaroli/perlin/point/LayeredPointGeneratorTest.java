package org.lefmaroli.perlin.point;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LayeredPointGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private LayeredPointGenerator defaultGenerator;
  private List<PointNoiseGenerator> layers;

  @BeforeEach
  void setup() {
    layers = new ArrayList<>(3);
    layers.add(new PointGenerator(2048, 1.0, System.currentTimeMillis()));
    layers.add(new PointGenerator(1024, 0.5, System.currentTimeMillis()));
    layers.add(new PointGenerator(512, 0.25, System.currentTimeMillis()));
    defaultGenerator = new LayeredPointGenerator(layers, null);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(1, defaultGenerator.getDimensions());
  }

  @Test
  void testCreateWithNoLayers() {
    ArrayList<PointNoiseGenerator> layers = new ArrayList<>(5);
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new LayeredPointGenerator(layers, null));
  }

  @Test
  void testGetNextBoundedValues() {
    for (int i = 0; i < 10000; i++) {
      Double pointNoiseData = defaultGenerator.getNext();
      Assertions.assertTrue(pointNoiseData <= 1.0);
      Assertions.assertTrue(pointNoiseData >= 0.0);
    }
  }

  @Test
  void testGetMaxAmplitude() {
    Assertions.assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testNumLayersGenerated() {
    Assertions.assertEquals(layers.size(), defaultGenerator.getNumberOfLayers(), 0);
  }

  @Test
  void testEquals() {
    LayeredPointGenerator sameGenerator = new LayeredPointGenerator(layers, null);
    Assertions.assertEquals(defaultGenerator, sameGenerator);
    Assertions.assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
  }

  @Test
  void testNotEquals() {
    List<PointNoiseGenerator> otherLayers = layers;
    otherLayers.add(new PointGenerator(8, 0.1, 5L));
    LayeredPointGenerator otherGenerator = new LayeredPointGenerator(otherLayers, null);
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(LayeredPointGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "scheduler",
            "jitterStrategy",
            "logger",
            "containers",
            "generated",
            "containersCount",
            "futures",
            "totalSize",
            "timeout",
            "executorService")
        .verify();
  }
}
