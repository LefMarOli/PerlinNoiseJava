package org.lefmaroli.perlin.line;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LayeredLineGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultLineLength = 125;
  private static final int numLayers = 3;
  private static final boolean isCircularDefault = false;
  private LayeredLineGenerator defaultGenerator;
  private List<LineNoiseGenerator> layers;

  @BeforeEach
  void setup() {
    layers = new ArrayList<>(numLayers);
    layers.add(
        new LineGenerator(
            1 / 2048.0,
            1.0 / 2048,
            defaultLineLength,
            1.0,
            System.currentTimeMillis(),
            isCircularDefault));
    layers.add(
        new LineGenerator(
            1.0 / 1024,
            1.0 / 1024,
            defaultLineLength,
            0.5,
            System.currentTimeMillis(),
            isCircularDefault));
    layers.add(
        new LineGenerator(
            1.0 / 512,
            1.0 / 512,
            defaultLineLength,
            0.25,
            System.currentTimeMillis(),
            isCircularDefault));
    defaultGenerator = new LayeredLineGenerator(layers);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(2, defaultGenerator.getDimensions());
  }

  @Test
  void testCreateWithNoLayers() {
    ArrayList<LineNoiseGenerator> layers = new ArrayList<>(5);
    Assertions.assertThrows(IllegalArgumentException.class, () -> new LayeredLineGenerator(layers));
  }

  @Test
  void testCreateWithDifferentLineLengthLayers() {
    List<LineNoiseGenerator> newLayerSet = layers;
    newLayerSet.add(
        new LineGenerator(
            1 / 256.0,
            1.0 / 256,
            defaultLineLength + 5,
            0.1225,
            System.currentTimeMillis(),
            isCircularDefault));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new LayeredLineGenerator(newLayerSet));
  }

  @Test
  void testGetNext() {
    double[] nextLine = defaultGenerator.getNext();
    Assertions.assertEquals(defaultLineLength, nextLine.length, 0);
  }

  @Test
  void testGetNextBoundedValues() {
    double[] line = defaultGenerator.getNext();
    for (double value : line) {
      Assertions.assertTrue(value >= 0.0, "Actual value smaller than 0.0: " + value);
      Assertions.assertTrue(value <= 1.0, "Actual value greater than 1.0: " + value);
    }
  }

  @Test
  void testGetMaxAmplitude() {
    Assertions.assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testNumLayersGenerated() {
    Assertions.assertEquals(numLayers, defaultGenerator.getNumberOfLayers(), 0);
  }

  @Test
  void testEquals() {
    LayeredLineGenerator sameGenerator = new LayeredLineGenerator(layers);
    Assertions.assertEquals(defaultGenerator, sameGenerator);
    Assertions.assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
  }

  @Test
  void testNotEquals() {
    List<LineNoiseGenerator> otherLayers = layers;
    otherLayers.add(
        new LineGenerator(1.0 / 8, 1.0 / 8, defaultLineLength, 0.1, 5L, isCircularDefault));
    LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testLineLength() {
    Assertions.assertEquals(defaultLineLength, defaultGenerator.getLineLength());
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(LayeredLineGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "scheduler", "jitterStrategy", "logger", "containers", "generated", "containersCount")
        .verify();
  }

  @Test
  void testNonCircularity() {
    Assertions.assertFalse(defaultGenerator.isCircular());
  }

  @Test
  void testMixCircularity() {
    List<LineNoiseGenerator> otherLayers = layers;
    otherLayers.add(new LineGenerator(1.0 / 8, 1.0 / 8, defaultLineLength, 0.1, 5L, true));
    LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
    Assertions.assertFalse(otherGenerator.isCircular());
  }

  @Test
  void testCircular() {
    List<LineNoiseGenerator> otherLayers = new ArrayList<>(3);
    otherLayers.add(new LineGenerator(1.0 / 8, 1.0 / 8, defaultLineLength, 0.1, 5L, true));
    otherLayers.add(new LineGenerator(1.0 / 16, 1.0 / 16, defaultLineLength, 0.05, 2L, true));
    otherLayers.add(new LineGenerator(1.0 / 25, 1.0 / 25, defaultLineLength, 0.005, 1L, true));
    LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
    Assertions.assertTrue(otherGenerator.isCircular());
  }
}
