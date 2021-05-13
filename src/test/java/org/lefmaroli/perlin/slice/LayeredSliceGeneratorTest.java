package org.lefmaroli.perlin.slice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class LayeredSliceGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultSliceWidth = 200;
  private static final int defaultSliceHeight = 200;
  private static final boolean isCircularDefault = false;
  private LayeredSliceGenerator defaultGenerator;
  private List<SliceNoiseGenerator> layers;

  @Before
  public void setup() {
    layers = new ArrayList<>(3);
    layers.add(
        new SliceGenerator(
            1.0/100,
            1.0/100,
            1.0/100,
            defaultSliceWidth,
            defaultSliceHeight,
            1.0,
            System.currentTimeMillis(),
            isCircularDefault));
    layers.add(
        new SliceGenerator(
            1.0/50,
            1.0/50,
            1.0/50,
            defaultSliceWidth,
            defaultSliceHeight,
            0.5,
            System.currentTimeMillis(),
            isCircularDefault));
    layers.add(
        new SliceGenerator(
            1.0/25,
            1.0/25,
            1.0/25,
            defaultSliceWidth,
            defaultSliceHeight,
            0.25,
            System.currentTimeMillis(),
            isCircularDefault));
    defaultGenerator = new LayeredSliceGenerator(layers);
  }

  @Test
  public void testDimension() {
    assertEquals(3, defaultGenerator.getDimensions());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNoLayers() {
    new LayeredSliceGenerator(new ArrayList<>(5));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithDifferentSliceWidthLayers() {
    List<SliceNoiseGenerator> newLayerSet = layers;
    newLayerSet.add(
        new SliceGenerator(
            1.0/256,
            1.0/256,
            1.0/256,
            defaultSliceWidth + 5,
            defaultSliceHeight,
            0.1225,
            System.currentTimeMillis(),
            isCircularDefault));
    new LayeredSliceGenerator(newLayerSet);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithDifferentSliceHeightLayers() {
    List<SliceNoiseGenerator> newLayerSet = layers;
    newLayerSet.add(
        new SliceGenerator(
            1.0/256,
            1.0/256,
            1.0/256,
            defaultSliceWidth,
            defaultSliceHeight - 9,
            0.1225,
            System.currentTimeMillis(),
            isCircularDefault));
    new LayeredSliceGenerator(newLayerSet);
  }

  @Test
  public void testGetNextCount() {
    double[][] slice = defaultGenerator.getNext();
    assertEquals(defaultSliceWidth, slice.length, 0);
    for (double[] line : slice) {
      assertEquals(defaultSliceHeight, line.length, 0);
    }
  }

  @Test
  public void testGetNextBoundedValues() {
    double[][] slice = defaultGenerator.getNext();
    for (double[] lines : slice) {
      for (double value : lines) {
        assertTrue("Actual value smaller than 0.0: " + value, value >= 0.0);
        assertTrue("Actual value greater than 1.0:" + value, value <= 1.0);
      }
    }
  }

  @Test
  public void testGetMaxAmplitude() {
    assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  public void testNumLayersGenerated() {
    assertEquals(layers.size(), defaultGenerator.getNumberOfLayers(), 0);
  }

  @Test
  public void testEquals() {
    LayeredSliceGenerator sameGenerator = new LayeredSliceGenerator(layers);
    assertEquals(defaultGenerator, sameGenerator);
    assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
  }

  @Test
  public void testNotEquals() {
    List<SliceNoiseGenerator> otherLayers = layers;
    otherLayers.add(
        new SliceGenerator(
            1.0/8, 1.0/8, 1.0/8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, isCircularDefault));
    LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testSliceWidth() {
    assertEquals(defaultSliceWidth, defaultGenerator.getSliceWidth());
  }

  @Test
  public void testSliceHeight() {
    assertEquals(defaultSliceHeight, defaultGenerator.getSliceHeight());
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(LayeredSliceGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "scheduler", "jitterStrategy", "logger", "containers", "generated", "containersCount")
        .verify();
  }

  @Test
  public void testNonCircularity() {
    assertFalse(defaultGenerator.isCircular());
  }

  @Test
  public void testMixCircularity() {
    List<SliceNoiseGenerator> otherLayers = layers;
    otherLayers.add(
        new SliceGenerator(
            1.0 / 8, 1.0 / 8, 1.0 / 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, true));
    LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers);
    assertFalse(otherGenerator.isCircular());
  }

  @Test
  public void testCircular() {
    List<SliceNoiseGenerator> otherLayers = new ArrayList<>(3);
    otherLayers.add(
        new SliceGenerator(
            1.0 / 8, 1.0 / 8, 1.0 / 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, true));
    otherLayers.add(
        new SliceGenerator(
            1.0 / 16, 1.0 / 16, 1.0 / 16, defaultSliceWidth, defaultSliceHeight, 0.05, 2L, true));
    otherLayers.add(
        new SliceGenerator(
            1.0 / 25, 1.0 / 25, 1.0 / 25, defaultSliceWidth, defaultSliceHeight, 0.005, 1L, true));
    LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers);
    assertTrue(otherGenerator.isCircular());
  }
}
