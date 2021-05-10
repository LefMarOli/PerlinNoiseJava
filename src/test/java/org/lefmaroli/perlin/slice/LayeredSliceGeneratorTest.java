package org.lefmaroli.perlin.slice;

import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;

public class LayeredSliceGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static final int defaultSliceWidth = 500;
  private static final int defaultSliceHeight = 500;
  private static final boolean isCircularDefault = false;
  private LayeredSliceGenerator defaultGenerator;
  private List<SliceNoiseGenerator> layers;

  @Before
  public void setup() {
    layers = new ArrayList<>(3);
    layers.add(
        new SliceGenerator(
            100,
            100,
            100,
            defaultSliceWidth,
            defaultSliceHeight,
            1.0,
            System.currentTimeMillis(),
            isCircularDefault));
    layers.add(
        new SliceGenerator(
            50,
            50,
            50,
            defaultSliceWidth,
            defaultSliceHeight,
            0.5,
            System.currentTimeMillis(),
            isCircularDefault));
    layers.add(
        new SliceGenerator(
            25,
            25,
            25,
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
            256,
            256,
            256,
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
            256,
            256,
            256,
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
            8, 8, 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, isCircularDefault));
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

  @Ignore("Skipped, only used to visualize results")
  @Test
  public void visualize() {
    List<SliceNoiseGenerator> newLayers = new ArrayList<>(3);
    newLayers.add(
        new SliceGenerator(
            1.0 / 10,
            1.0 / 20,
            1.0 / 60,
            defaultSliceWidth,
            defaultSliceHeight,
            1.0,
            System.currentTimeMillis(),
            isCircularDefault));
    newLayers.add(
        new SliceGenerator(
            1.0 / 20,
            1.0 / 120,
            1.0 / 30,
            defaultSliceWidth,
            defaultSliceHeight,
            0.8,
            System.currentTimeMillis(),
            isCircularDefault));
    newLayers.add(
        new SliceGenerator(
            1.0 / 15,
            1.0 / 50,
            1.0 / 50,
            defaultSliceWidth,
            defaultSliceHeight,
            0.25,
            System.currentTimeMillis(),
            isCircularDefault));
    SliceNoiseGenerator generator = new LayeredSliceGenerator(newLayers);
    double[][] slices = generator.getNext();
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(slices, 5);
    image.setVisible();

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> scheduledFuture =
        ses.scheduleAtFixedRate(
            () -> {
              double[][] newSlice = generator.getNext();
              image.updateImage(newSlice);
            },
            5,
            300,
            TimeUnit.MILLISECONDS);

    int testDurationInMs = 500;
    ses.schedule(
        () -> {
          scheduledFuture.cancel(true);
          ses.shutdownNow();
        },
        testDurationInMs,
        TimeUnit.SECONDS);

    waitAtMost(testDurationInMs + 1, TimeUnit.SECONDS).until(ses::isShutdown);
  }
}
