package org.lefmaroli.interpolation;

import junit.framework.TestCase;

public class InterpolationTest extends TestCase {

    public void testLinear() {
        //1 and 2 as values, distance of 0.5
        assertEquals(1.5, Interpolation.linear(1, 2, 0.5), 0);
        assertEquals(1.25, Interpolation.linear(1, 2, 0.25), 0);
    }

    public void testLinearWithFade() {
        //1.5 should not change here
        assertEquals(1.5, Interpolation.linearWithFade(1,2,0.5), 0);
        assertEquals(1.103515625, Interpolation.linearWithFade(1,2,0.25), 0);

    }

    public void testFade() {
        //0.5 should return exactly 0.5
        assertEquals(0.5, Interpolation.fade(0.5), 0);
        assertEquals(0.103515625, Interpolation.fade(0.25), 0);
    }
}