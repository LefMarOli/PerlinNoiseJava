package org.lefmaroli.perlin.slice;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.SimpleGrayScaleImage;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class SliceGeneratorTest {

    private SliceGenerator defaultGenerator;
    private static final int noiseInterpolationPoints = 100;
    private static final int widthInterpolationPoints = 100;
    private static final int heightInterpolationPoints = 100;
    private static final int sliceWidth = 700;
    private static final int sliceHeight = 700;
    private final long randomSeed = System.currentTimeMillis();
    private static final double maxAmplitude = 1.0;
    private static final boolean isCircular = false;
    int requestedCount = 100;

    @Before
    public void setup() {
        defaultGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                        sliceWidth, sliceHeight, maxAmplitude, randomSeed, isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidNoiseInterpolationPoints() {
        new SliceGenerator(-5, widthInterpolationPoints, heightInterpolationPoints,
                sliceWidth, sliceHeight, maxAmplitude, randomSeed, isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidWidthInterpolationPoints() {
        new SliceGenerator(noiseInterpolationPoints, -4, heightInterpolationPoints,
                sliceWidth, sliceHeight, maxAmplitude, randomSeed, isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidHeightInterpolationPoints() {
        new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, -5,
                sliceWidth, sliceHeight, maxAmplitude, randomSeed, isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidSliceWidth() {
        new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                -9, sliceHeight, maxAmplitude, randomSeed, isCircular);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidSliceHeight() {
        new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                sliceWidth, -7, maxAmplitude, randomSeed, isCircular);
    }

    @Test
    public void getNextSlicesCorrectSize() {
        List<SliceNoiseData> noiseData = defaultGenerator.getNext(requestedCount).getAsList();
        assertEquals(requestedCount, noiseData.size(), 0);
        for (SliceNoiseData slice : noiseData) {
            assertEquals(sliceWidth, slice.getSliceWidth(), 0);
            assertEquals(sliceHeight, slice.getSliceHeight(), 0);
        }
    }

    @Test
    public void testGetNoiseInterpolationPoints() {
        assertEquals(noiseInterpolationPoints, defaultGenerator.getNoiseInterpolationPoints());
    }

    @Test
    public void testGetWidthInterpolationPoints() {
        assertEquals(widthInterpolationPoints, defaultGenerator.getWidthInterpolationPoints());
    }

    @Test
    public void testGetHeightInterpolationPoints() {
        assertEquals(heightInterpolationPoints, defaultGenerator.getHeightInterpolationPoints());
    }

    @Test
    public void testGetSliceWidth() {
        assertEquals(sliceWidth, defaultGenerator.getSliceWidth());
    }

    @Test
    public void testGetSliceHeight() {
        assertEquals(sliceHeight, defaultGenerator.getSliceHeight());
    }

    @Test
    public void testGetMaxAmplitude() {
        assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNextLinesInvalidCount() {
        defaultGenerator.getNext(-5);
    }

    @Test
    public void testValuesBounded() {
        double[][][] slices = defaultGenerator.getNext(requestedCount).getAsRawData();
        for (double[][] slice : slices) {
            for (double[] line : slice) {
                for (double value : line) {
                    assertTrue("Value " + value + "not bounded by 0", value > 0.0);
                    assertTrue("Value " + value + "not bounded by max amplitude", value < maxAmplitude);
                }
            }
        }
    }

    @Test
    public void testValuesMultipliedByMaxAmplitude() {
        Random random = new Random(System.currentTimeMillis());
        double newMaxAmplitude = random.nextDouble() * 100;
        SliceGenerator amplifiedLayer =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                        sliceWidth, sliceHeight, newMaxAmplitude, randomSeed, isCircular);

        double[][][] slices = defaultGenerator.getNext(requestedCount).getAsRawData();
        double[][][] amplifiedSlices = amplifiedLayer.getNext(requestedCount).getAsRawData();

        for (double[][] slice : slices) {
            for (double[] lines : slice) {
                for (int j = 0; j < lines.length; j++) {
                    lines[j] = lines[j] * newMaxAmplitude;
                }
            }
        }

        for (int i = 0; i < slices.length; i++) {
            for (int j = 0; j < slices[0].length; j++) {
                assertExpectedArrayEqualsActual(slices[i][j], amplifiedSlices[i][j], 1e-18);
            }
        }
    }

    private static void assertExpectedArrayEqualsActual(double[] expected, double[] actual, double delta) {
        assertEquals(expected.length, actual.length, delta);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], delta);
        }
    }

    @Test
    public void testCreateSameGeneratedSlices() {
        SliceGenerator same =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                        sliceWidth, sliceHeight, maxAmplitude, randomSeed, isCircular);
        double[][][] nextSegment1 = defaultGenerator.getNext(requestedCount).getAsRawData();
        double[][][] nextSegment2 = same.getNext(requestedCount).getAsRawData();

        assertEquals(nextSegment1.length, nextSegment2.length, 0);
        assertEquals(nextSegment1[0].length, nextSegment2[0].length, 0);
        for (int i = 0; i < nextSegment1.length; i++) {
            for (int j = 0; j < nextSegment1[0].length; j++) {
                assertExpectedArrayEqualsActual(nextSegment1[i][j], nextSegment2[i][j], 0.0);
            }
        }
    }

    @Test
    public void testEquals() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed, isCircular);
        assertEquals(defaultGenerator, otherGenerator);
        assertEquals(defaultGenerator.hashCode(), otherGenerator.hashCode());
    }

    @Test
    public void testNotEqualsNotSameNoiseInterpolationPoints() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints + 6, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameWidthInterpolationPoints() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints + 9,
                        heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameHeightInterpolationPoints() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints + 15, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameSliceWidth() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth + 5, sliceHeight,
                        maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameSliceHeight() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth, sliceHeight + 6,
                        maxAmplitude, randomSeed, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameMaxAmplitude() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude * 1.5, randomSeed, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameRandomSeed() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed + 5L, isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testNotEqualsNotSameCircularity() {
        SliceGenerator otherGenerator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints,
                        heightInterpolationPoints, sliceWidth, sliceHeight,
                        maxAmplitude, randomSeed, !isCircular);
        assertNotEquals(defaultGenerator, otherGenerator);
    }

    @Test
    public void testToString() {
        ToStringVerifier.forClass(SliceGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .withIgnoredFields("randomGenerator", "generated", "randomBoundsXCount", "randomBoundsYCount",
                        "previousBounds")
                .verify();
    }

    @Ignore
    @Test
    public void testCircularity(){
        SliceGenerator generator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                        sliceWidth, sliceHeight, 1.0, System.currentTimeMillis(), true);

    }

    @Ignore
    @Test
    public void testVisualizeMorphingImage() throws InterruptedException {
        SliceGenerator generator =
                new SliceGenerator(noiseInterpolationPoints, widthInterpolationPoints, heightInterpolationPoints,
                        sliceWidth, sliceHeight, 1.0, System.currentTimeMillis(), false);
        int count = 500;
        double[][][] slices = generator.getNext(count).getAsRawData();
        SimpleGrayScaleImage image = new SimpleGrayScaleImage(slices[0], 1);
        image.setVisible();
        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 1) {
                previousTime = System.currentTimeMillis();
                double[][][] newSlices = generator.getNext(1).getAsRawData();
                image.updateImage(newSlices[0]);
            }else{
                Thread.sleep(1);
            }
        }
    }
}