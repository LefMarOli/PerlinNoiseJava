package org.lefmaroli.perlin.slice;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LayeredSliceGeneratorTest {

    private static final double maxAmplitude = 1.75;
    private static final int defaultSliceWidth = 250;
    private static final int defaultSliceHeight = 250;
    private LayeredSliceGenerator defaultGenerator;
    private List<SliceNoiseGenerator> layers;
    private boolean isCircularDefault = false;

    @Before
    public void setup() {
        layers = new ArrayList<>(3);
        layers.add(
                new SliceGenerator(100, 100, 100, defaultSliceWidth, defaultSliceHeight, 1.0,
                        System.currentTimeMillis(), isCircularDefault));
        layers.add(
                new SliceGenerator(50, 50, 50, defaultSliceWidth, defaultSliceHeight, 0.5, System.currentTimeMillis(),
                        isCircularDefault));
        layers.add(new SliceGenerator(25, 25, 25, defaultSliceWidth, defaultSliceHeight,
                0.25, System.currentTimeMillis(), isCircularDefault));
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
        newLayerSet.add(new SliceGenerator(256, 256, 256, defaultSliceWidth + 5, defaultSliceHeight, 0.1225,
                System.currentTimeMillis(),
                isCircularDefault));
        new LayeredSliceGenerator(newLayerSet);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithDifferentSliceHeightLayers() {
        List<SliceNoiseGenerator> newLayerSet = layers;
        newLayerSet.add(new SliceGenerator(256, 256, 256, defaultSliceWidth, defaultSliceHeight - 9, 0.1225,
                System.currentTimeMillis(),
                isCircularDefault));
        new LayeredSliceGenerator(newLayerSet);
    }

    @Test
    public void testGetNextCount() {
        int expectedCount = 75;
        double[][][] slices = defaultGenerator.getNext(expectedCount).getAsRawData();
        assertEquals(expectedCount, slices.length, 0);
        for (double[][] slice : slices) {
            assertEquals(defaultSliceWidth, slice.length, 0);
            for (double[] line : slice) {
                assertEquals(defaultSliceHeight, line.length, 0);
            }
        }
    }

    @Test
    public void testGetNextBoundedValues() {
        double[][][] slices = defaultGenerator.getNext(100).getAsRawData();
        for (double[][] slice : slices) {
            for (double[] lines : slice) {
                for (double value : lines) {
                    assertTrue("Actual value smaller than 0.0: " + value, value >= 0.0);
                    assertTrue("Actual value greater than max amplitude of " + maxAmplitude + ":" + value,
                            value <= maxAmplitude);
                }
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
        LayeredSliceGenerator sameGenerator = new LayeredSliceGenerator(layers);
        assertEquals(defaultGenerator, sameGenerator);
        assertEquals(defaultGenerator.hashCode(), sameGenerator.hashCode());
    }

    @Test
    public void testNotEquals() {
        List<SliceNoiseGenerator> otherLayers = layers;
        otherLayers.add(new SliceGenerator(8, 8, 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, isCircularDefault));
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
                .withIgnoredFields("scheduler", "jitterStrategy")
                .verify();
    }

    @Test
    public void testNonCircularity() {
        assertFalse(defaultGenerator.isCircular());
    }

    @Test
    public void testMixCircularity() {
        List<SliceNoiseGenerator> otherLayers = layers;
        otherLayers.add(new SliceGenerator(8, 8, 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, true));
        LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers);
        assertFalse(otherGenerator.isCircular());
    }

    @Test
    public void testCircular() {
        List<SliceNoiseGenerator> otherLayers = new ArrayList<>(3);
        otherLayers.add(new SliceGenerator(8, 8, 8, defaultSliceWidth, defaultSliceHeight, 0.1, 5L, true));
        otherLayers.add(new SliceGenerator(16, 16, 16, defaultSliceWidth, defaultSliceHeight, 0.05, 2L, true));
        otherLayers.add(new SliceGenerator(25, 25, 25, defaultSliceWidth, defaultSliceHeight, 0.005, 1L, true));
        LayeredSliceGenerator otherGenerator = new LayeredSliceGenerator(otherLayers);
        assertTrue(otherGenerator.isCircular());
    }

    @Ignore
    @Test
    public void visualize() throws InterruptedException {
        List<SliceNoiseGenerator> newLayers = new ArrayList<>(3);
        newLayers.add(
                new SliceGenerator(100, 20, 60, defaultSliceWidth, defaultSliceHeight, 1.0,
                        System.currentTimeMillis(), isCircularDefault));
        newLayers.add(
                new SliceGenerator(20, 120, 30, defaultSliceWidth, defaultSliceHeight, 0.8, System.currentTimeMillis(),
                        isCircularDefault));
        newLayers.add(new SliceGenerator(500, 50, 50, defaultSliceWidth, defaultSliceHeight,
                0.25, System.currentTimeMillis(), isCircularDefault));
        SliceNoiseGenerator generator = new LayeredSliceGenerator(newLayers);
        int count = 1;
        double[][][] slices = generator.getNext(count).getAsRawData();
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(slices[0], 1);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                double[][][] newSlices = generator.getNext(1).getAsRawData();
                image.updateImage(newSlices[0]);
            } else {
                Thread.sleep(1);
            }
        }
    }
}