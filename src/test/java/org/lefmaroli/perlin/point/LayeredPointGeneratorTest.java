package org.lefmaroli.perlin.point;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class LayeredPointGeneratorTest {

  private LayeredPointGenerator defaultGenerator;
  private List<PointNoiseGenerator> layers;
  private double maxAmplitude = 1.75;

  @Before
  public void setup() {
    layers = new ArrayList<>(3);
    layers.add(new PointGenerator(2048, 1.0, System.currentTimeMillis()));
    layers.add(new PointGenerator(1024, 0.5, System.currentTimeMillis()));
    layers.add(new PointGenerator(512, 0.25, System.currentTimeMillis()));
    defaultGenerator = new LayeredPointGenerator(layers);
  }

  @Test
  public void testDimension() {
    assertEquals(1, defaultGenerator.getDimensions());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNoLayers() {
    new LayeredPointGenerator(new ArrayList<>(5));
  }

  @Test
  public void testGetNextCount() {
    int expectedCount = 75;
    PointNoiseData[] nextSegment = defaultGenerator.getNext(expectedCount).getAsArray();
    assertEquals(expectedCount, nextSegment.length, 0);
  }

  @Test
  public void testGetNextBoundedValues() {
    PointNoiseData[] nextSegment = defaultGenerator.getNext(10000).getAsArray();
    for (PointNoiseData pointNoiseData : nextSegment) {
      assertTrue(pointNoiseData.getAsRawData() <= 1.0);
      assertTrue(pointNoiseData.getAsRawData() >= 0.0);
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
    LayeredPointGenerator sameGenerator = new LayeredPointGenerator(layers);
    assertEquals(defaultGenerator, sameGenerator);
    assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
  }

  @Test
  public void testNotEquals() {
    List<PointNoiseGenerator> otherLayers = layers;
    otherLayers.add(new PointGenerator(8, 0.1, 5L));
    LayeredPointGenerator otherGenerator = new LayeredPointGenerator(otherLayers);
    assertNotEquals(defaultGenerator, otherGenerator);
  }

  @Test
  public void testToString() {
    ToStringVerifier.forClass(LayeredPointGenerator.class)
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields("scheduler", "jitterStrategy")
        .verify();
  }
}