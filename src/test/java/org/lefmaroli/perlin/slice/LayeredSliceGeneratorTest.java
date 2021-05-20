package org.lefmaroli.perlin.slice;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LayeredSliceGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultSliceWidth = 200;
  private static final int defaultSliceHeight = 200;
  private static final boolean isCircularDefault = false;
  private LayeredSliceGenerator defaultGenerator;
  private List<SliceNoiseGenerator> layers;

  @BeforeEach
  void setup() {
    layers = new ArrayList<>(3);
    layers.add(
        new SliceGenerator(
            1.0 / 100,
            1.0 / 100,
            1.0 / 100,
            defaultSliceWidth,
            defaultSliceHeight,
            1.0,
            System.currentTimeMillis(),
            isCircularDefault,
            null));
    layers.add(
        new SliceGenerator(
            1.0 / 50,
            1.0 / 50,
            1.0 / 50,
            defaultSliceWidth,
            defaultSliceHeight,
            0.5,
            System.currentTimeMillis(),
            isCircularDefault,
            null));
    layers.add(
        new SliceGenerator(
            1.0 / 25,
            1.0 / 25,
            1.0 / 25,
            defaultSliceWidth,
            defaultSliceHeight,
            0.25,
            System.currentTimeMillis(),
            isCircularDefault,
            null));
    defaultGenerator = new LayeredSliceGenerator(layers, null);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(3, defaultGenerator.getDimensions());
  }

  @Test
  void testCreateWithNoLayers() {
    ArrayList<SliceNoiseGenerator> layers = new ArrayList<>(5);
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new LayeredSliceGenerator(layers, null));
  }

  @Test
  void testCreateWithDifferentSliceWidthLayers() {
    List<SliceNoiseGenerator> newLayerSet = layers;
    newLayerSet.add(
        new SliceGenerator(
            1.0 / 256,
            1.0 / 256,
            1.0 / 256,
            defaultSliceWidth + 5,
            defaultSliceHeight,
            0.1225,
            System.currentTimeMillis(),
            isCircularDefault,
            null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new LayeredSliceGenerator(newLayerSet, null));
  }

  @Test
  void testCreateWithDifferentSliceHeightLayers() {
    List<SliceNoiseGenerator> newLayerSet = layers;
    newLayerSet.add(
        new SliceGenerator(
            1.0 / 256,
            1.0 / 256,
            1.0 / 256,
            defaultSliceWidth,
            defaultSliceHeight - 9,
            0.1225,
            System.currentTimeMillis(),
            isCircularDefault,
            null));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> new LayeredSliceGenerator(newLayerSet, null));
  }

  @Test
  void testGetNextCount() {
    double[][] slice = defaultGenerator.getNext();
    Assertions.assertEquals(defaultSliceWidth, slice.length, 0);
    for (double[] line : slice) {
      Assertions.assertEquals(defaultSliceHeight, line.length, 0);
    }
  }

  @Test
  void testGetNextBoundedValues() {
    double[][] slice = defaultGenerator.getNext();
    for (double[] lines : slice) {
      for (double value : lines) {
        Assertions.assertTrue(value >= 0.0, "Actual value smaller than 0.0: " + value);
        Assertions.assertTrue(value <= 1.0, "Actual value greater than 1.0:" + value);
      }
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
    LayeredSliceGenerator sameGenerator = new LayeredSliceGenerator(layers, null);
    Assertions.assertEquals(defaultGenerator, sameGenerator);
    Assertions.assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
  }

  @Test
  void testNotEquals() {
    List<SliceNoiseGenerator> otherLayers = layers;
    otherLayers.add(
        new SliceGenerator(
            1.0 / 8,
            1.0 / 8,
            1.0 / 8,
            defaultSliceWidth,
            defaultSliceHeight,
            0.1,
            5L,
            isCircularDefault,
            null));
    LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers, null);
    Assertions.assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  void testSliceWidth() {
    Assertions.assertEquals(defaultSliceWidth, defaultGenerator.getSliceWidth());
  }

  @Test
  void testSliceHeight() {
    Assertions.assertEquals(defaultSliceHeight, defaultGenerator.getSliceHeight());
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(LayeredSliceGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "scheduler",
            "jitterStrategy",
            "logger",
            "containers",
            "generated",
            "containersCount",
            "pool")
        .verify();
  }

  @Test
  void testNonCircularity() {
    Assertions.assertFalse(defaultGenerator.isCircular());
  }

  @Test
  void testMixCircularity() {
    List<SliceNoiseGenerator> otherLayers = layers;
    otherLayers.add(
        new SliceGenerator(
            1.0 / 8, 1.0 / 8, 1.0 / 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, true, null));
    LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers, null);
    Assertions.assertFalse(otherGenerator.isCircular());
  }

  @Test
  void testCircular() {
    List<SliceNoiseGenerator> otherLayers = new ArrayList<>(3);
    otherLayers.add(
        new SliceGenerator(
            1.0 / 8, 1.0 / 8, 1.0 / 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, true, null));
    otherLayers.add(
        new SliceGenerator(
            1.0 / 16,
            1.0 / 16,
            1.0 / 16,
            defaultSliceWidth,
            defaultSliceHeight,
            0.05,
            2L,
            true,
            null));
    otherLayers.add(
        new SliceGenerator(
            1.0 / 25,
            1.0 / 25,
            1.0 / 25,
            defaultSliceWidth,
            defaultSliceHeight,
            0.005,
            1L,
            true,
            null));
    LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers, null);
    Assertions.assertTrue(otherGenerator.isCircular());
  }
}
