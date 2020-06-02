package org.lefmaroli.perlin.point;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PointNoiseDataTest {

    @Test
    public void testHashCode() {
        double value = 45.89746;
        PointNoiseData first = new PointNoiseData(value);
        PointNoiseData other = new PointNoiseData(value);
        assertEquals(first.hashCode(), other.hashCode());
    }

}