package org.lefmaroli.perlin.point;


import org.junit.Test;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PointNoiseGeneratorBuilderTest {

    @Test
    public void testBuildNoisePointNotNull() throws NoiseBuilderException {
        PointNoiseGenerator noisePointGenerator = new PointNoiseGeneratorBuilder().build();
        assertNotNull(noisePointGenerator);
    }

    @Test
    public void testBuildNoisePointCreateSameFromSameBuilder() throws NoiseBuilderException {
        PointNoiseGeneratorBuilder pointNoiseGeneratorBuilder = new PointNoiseGeneratorBuilder();
        PointNoiseGenerator noisePointGenerator = pointNoiseGeneratorBuilder.build();
        PointNoiseGenerator noisePointGenerator2 = pointNoiseGeneratorBuilder.build();
        assertNotNull(noisePointGenerator2);
        assertEquals(noisePointGenerator, noisePointGenerator2);
    }

}