package org.lefmaroli.perlin.line;

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

public class LayeredLineGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultLineLength = 125;
  private LayeredLineGenerator defaultGenerator;
  private List<LineNoiseGenerator> layers;
  private boolean isCircularDefault = false;

  @Before
  public void setup() {
    layers = new ArrayList<>(3);
    layers.add(
        new LineGenerator(
            2048, 2048, defaultLineLength, 1.0, System.currentTimeMillis(), isCircularDefault));
    layers.add(
        new LineGenerator(
            1024, 1024, defaultLineLength, 0.5, System.currentTimeMillis(), isCircularDefault));
    layers.add(
        new LineGenerator(
            512, 512, defaultLineLength, 0.25, System.currentTimeMillis(), isCircularDefault));
    defaultGenerator = new LayeredLineGenerator(layers);
  }

  @Test
  public void testDimension() {
    assertEquals(2, defaultGenerator.getDimensions());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNoLayers() {
    new LayeredLineGenerator(new ArrayList<>(5));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithDifferentLineLengthLayers() {
    List<LineNoiseGenerator> newLayerSet = layers;
    newLayerSet.add(
        new LineGenerator(
            256,
            256,
            defaultLineLength + 5,
            0.1225,
            System.currentTimeMillis(),
            isCircularDefault));
    new LayeredLineGenerator(newLayerSet);
  }

  @Test
  public void testGetNextCount() {
    int expectedCount = 10;
    double[][] nextLines = defaultGenerator.getNext(expectedCount).getAsRawData();
    assertEquals(expectedCount, nextLines.length, 0);
    for (double[] line : nextLines) {
      assertEquals(defaultLineLength, line.length, 0);
    }
  }

  @Test
  public void testGetNextBoundedValues() {
    double[][] lines = defaultGenerator.getNext(10).getAsRawData();
    for (double[] line : lines) {
      for (double value : line) {
        assertTrue("Actual value smaller than 0.0: " + value, value >= 0.0);
        assertTrue(value <= maxAmplitude);
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

  @Test(expected = IllegalArgumentException.class)
  public void testGetNextNegativeCount() {
    defaultGenerator.getNext(-6);
  }

  @Test
  public void testEquals() {
    LayeredLineGenerator sameGenerator = new LayeredLineGenerator(layers);
    assertEquals(defaultGenerator, sameGenerator);
    assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
  }

  @Test
  public void testNotEquals() {
    List<LineNoiseGenerator> otherLayers = layers;
    otherLayers.add(new LineGenerator(8, 8, defaultLineLength, 0.1, 5L, isCircularDefault));
    LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testLineLength() {
    assertEquals(defaultLineLength, defaultGenerator.getLineLength());
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(LayeredLineGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields("scheduler", "jitterStrategy")
        .verify();
  }

  @Test
  public void testNonCircularity() {
    assertFalse(defaultGenerator.isCircular());
  }

  @Test
  public void testMixCircularity() {
    List<LineNoiseGenerator> otherLayers = layers;
    otherLayers.add(new LineGenerator(8, 8, defaultLineLength, 0.1, 5L, true));
    LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
    assertFalse(otherGenerator.isCircular());
  }

  @Test
  public void testCircular() {
    List<LineNoiseGenerator> otherLayers = new ArrayList<>(3);
    otherLayers.add(new LineGenerator(8, 8, defaultLineLength, 0.1, 5L, true));
    otherLayers.add(new LineGenerator(16, 16, defaultLineLength, 0.05, 2L, true));
    otherLayers.add(new LineGenerator(25, 25, defaultLineLength, 0.005, 1L, true));
    LayeredLineGenerator otherGenerator = new LayeredLineGenerator(otherLayers);
    assertTrue(otherGenerator.isCircular());
  }
}
