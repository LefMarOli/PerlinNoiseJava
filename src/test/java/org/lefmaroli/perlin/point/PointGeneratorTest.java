package org.lefmaroli.perlin.point;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class PointGeneratorTest {

    private final int interpolationPoints = 50;
    private final int expectedCount = 500;
    private final long randomSeed = System.currentTimeMillis();
    private PointGenerator defaultGenerator;

    @Before
    public void setup() {
        defaultGenerator = new PointGenerator(interpolationPoints, 1.0, randomSeed);
    }

    @Test
    public void testDimension() {
        assertEquals(1, defaultGenerator.getDimensions());
    }

    @Test
    public void testGetNextSegmentCount() {
        Double[] nextSegment = defaultGenerator.getNext(expectedCount);
        assertEquals(expectedCount, nextSegment.length, 0);
    }

    @Test
    public void testValuesBounded() {
        Double[] nextSegment = defaultGenerator.getNext(expectedCount);
        for (Double value : nextSegment) {
            assertNotNull(value);
            assertTrue(value < 1.0);
            assertTrue(value > 0.0);
        }
    }

    @Test
    public void testValuesMultipliedByFactor() {
        Random random = new Random(System.currentTimeMillis());
        double amplitudeFactor = random.nextDouble() * 100;
        PointGenerator amplifiedLayer = new PointGenerator(interpolationPoints, amplitudeFactor, randomSeed);

        Double[] values = defaultGenerator.getNext(expectedCount);
        Double[] actualAmplifiedValues = amplifiedLayer.getNext(expectedCount);

        Double[] expectedAmplifiedValues = new Double[values.length];
        for (int i = 0; i < values.length; i++) {
            expectedAmplifiedValues[i] = values[i] * amplitudeFactor;
        }
        assertExpectedArrayEqualsActual(expectedAmplifiedValues, actualAmplifiedValues, 1e-18);
    }

    @Test
    public void testCreateSamePoints() {
        PointGenerator sameLayer = new PointGenerator(50, 1.0, randomSeed);
        Double[] nextSegment1 = defaultGenerator.getNext(expectedCount);
        Double[] nextSegment2 = sameLayer.getNext(expectedCount);
        assertExpectedArrayEqualsActual(nextSegment1, nextSegment2, 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate() {
        new PointGenerator(-5, 1.0, 0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCount() {
        PointGenerator layer = new PointGenerator(5, 1.0, 0L);
        layer.getNext(-5);
    }

    @Test
    public void testEquals() {
        PointGenerator layer = new PointGenerator(5, 1.0, 0L);
        PointGenerator layer2 = new PointGenerator(5, 1.0, 0L);
        assertEquals(layer, layer2);
    }

    @Test
    public void testNotEqualNotSameSeed() {
        PointGenerator layer = new PointGenerator(5, 1.0, 0L);
        PointGenerator layer2 = new PointGenerator(5, 1.0, 1L);
        assertNotEquals(layer, layer2);
    }

    @Test
    public void testNotEqualNotSameAmplitude() {
        PointGenerator layer = new PointGenerator(5, 1.0, 0L);
        PointGenerator layer2 = new PointGenerator(5, 2.0, 0L);
        assertNotEquals(layer, layer2);
    }

    @Test
    public void testNotEqualNotSameInterpolationPoints() {
        PointGenerator layer = new PointGenerator(5, 1.0, 0L);
        PointGenerator layer2 = new PointGenerator(6, 1.0, 0L);
        assertNotEquals(layer, layer2);
    }

    @Test
    public void testHashCode() {
        PointGenerator layer = new PointGenerator(5, 1.0, 0L);
        PointGenerator layer2 = new PointGenerator(5, 1.0, 0L);
        assertEquals(layer, layer2);
        assertEquals(layer.hashCode(), layer2.hashCode());
    }

    @Test
    public void testToString() {
        ToStringVerifier.forClass(PointGenerator.class)
                .withClassName(NameStyle.SIMPLE_NAME)
                .withPreset(Presets.INTELLI_J)
                .withIgnoredFields("randomGenerator", "previousBound", "generated", "segmentLength")
                .verify();
    }

    @Test
    public void getInterpolationPointsCount() {
        assertEquals(interpolationPoints, defaultGenerator.getNoiseInterpolationPointsCount());
    }

    private static void assertExpectedArrayEqualsActual(Double[] expected, Double[] actual, double delta) {
        assertEquals(expected.length, actual.length, delta);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], delta);
        }
    }
}