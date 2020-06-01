package org.lefmaroli.interpolation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InterpolationTest {

    @Test
    public void testLinearSmallToBigInterpolation() {
        //1 and 2 as values, distance of 0.5
        assertEquals(1.5, Interpolation.linear(1, 2, 0.5), 0.0);
        assertEquals(1.25, Interpolation.linear(1, 2, 0.25), 0.0);
    }

    @Test
    public void testLinearBigToSmallInterpolation() {
        //1 and 2 as values, distance of 0.5
        assertEquals(1.5, Interpolation.linear(2, 1, 0.5), 0.0);
        assertEquals(1.75, Interpolation.linear(2, 1, 0.25), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLinearIllegalValue() {
        Interpolation.linear(1, 5, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLinearIllegalValue2() {
        Interpolation.linear(1, 5, 5);
    }

    @Test
    public void testLinearWithFade() {
        //1.5 should not change here
        assertEquals(1.5, Interpolation.linearWithFade(1, 2, 0.5), 0);
        assertEquals(1.103515625, Interpolation.linearWithFade(1, 2, 0.25), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLinearWithFadeIllegalValue() {
        Interpolation.linearWithFade(1, 5, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLinearWithFadeIllegalValue2() {
        Interpolation.linearWithFade(1, 5, 5);
    }

    @Test
    public void testFade() {
        //0.5 should return exactly 0.5
        assertEquals(0.5, Interpolation.fade(0.5), 0);
        assertEquals(0.103515625, Interpolation.fade(0.25), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFadeIllegalValue() {
        Interpolation.fade(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFadeIllegalValue2() {
        Interpolation.fade(5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DIllegalMux() {
        Interpolation.linear2D(0, 1, 0, 1, -6, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DIllegalMux2() {
        Interpolation.linear2D(0, 1, 0, 1, 4, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DIllegalMuy() {
        Interpolation.linear2D(0, 1, 0, 1, 0.5, -8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DIllegalMuy2() {
        Interpolation.linear2D(0, 1, 0, 1, 0.5, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DWithFadeIllegalMux() {
        Interpolation.linear2DWithFade(0, 1, 0, 1, -6, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DWithFadeIllegalMux2() {
        Interpolation.linear2DWithFade(0, 1, 0, 1, 4, 0.5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DWithFadeIllegalMuy() {
        Interpolation.linear2DWithFade(0, 1, 0, 1, 0.5, -8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test2DWithFadeIllegalMuy2() {
        Interpolation.linear2DWithFade(0, 1, 0, 1, 0.5, 4);
    }

    @Test
    public void test2DWithFadeTrivial() {
        //0----1
        //|    |
        //1----0
        assertEquals(0.5, Interpolation.linear2DWithFade(0, 1, 1, 0, 0.5, 0.5), 0.0);
        assertEquals(0.5, Interpolation.linear2DWithFade(0, 1, 1, 0, 0, 0.5), 0.0);
        assertEquals(0.5, Interpolation.linear2DWithFade(0, 1, 1, 0, 1, 0.5), 0.0);
        assertEquals(0.5, Interpolation.linear2DWithFade(0, 1, 1, 0, 0.5, 0), 0.0);
        assertEquals(0.5, Interpolation.linear2DWithFade(0, 1, 1, 0, 0.5, 1), 0.0);

        assertEquals(0.2729698272, Interpolation.linear2DWithFade(0, 1, 1, 0, 0.3, 0.3), 1E-9);
    }

    @Test
    public void test2DTrivial() {
        //0----1
        //|    |
        //1----0
        assertEquals(0.5, Interpolation.linear2D(0, 1, 1, 0, 0.5, 0.5), 0.0);
        assertEquals(0.5, Interpolation.linear2D(0, 1, 1, 0, 0, 0.5), 0.0);
        assertEquals(0.5, Interpolation.linear2D(0, 1, 1, 0, 1, 0.5), 0.0);
        assertEquals(0.5, Interpolation.linear2D(0, 1, 1, 0, 0.5, 0), 0.0);
        assertEquals(0.5, Interpolation.linear2D(0, 1, 1, 0, 0.5, 1), 0.0);
        assertEquals(0.42, Interpolation.linear2D(0, 1, 1, 0, 0.3, 0.3), 1E-9);
    }

    @Test
    public void test2DNonTrivial() {
        //0.3----10.9
        // |      |
        //-7------64.3
        assertEquals(17.125, Interpolation.linear2D(0.3, -7, 10.9, 64.3, 0.5, 0.5), 1E-9);
        assertEquals(-3.35, Interpolation.linear2D(0.3, -7, 10.9, 64.3, 0, 0.5), 1E-9);
        assertEquals(37.6, Interpolation.linear2D(0.3, -7, 10.9, 64.3, 1, 0.5), 1E-9);
        assertEquals(5.6, Interpolation.linear2D(0.3, -7, 10.9, 64.3, 0.5, 0), 1E-9);
        assertEquals(28.65, Interpolation.linear2D(0.3, -7, 10.9, 64.3, 0.5, 1), 1E-9);
        assertEquals(6.753, Interpolation.linear2D(0.3, -7, 10.9, 64.3, 0.3, 0.3), 1E-9);
    }

    @Test
    public void test2DWithFadeNonTrivial() {
        //0.3----10.9
        // |      |
        //-7------64.3
        assertEquals(17.125, Interpolation.linear2DWithFade(0.3, -7, 10.9, 64.3, 0.5, 0.5), 1E-9);
        assertEquals(-3.35, Interpolation.linear2DWithFade(0.3, -7, 10.9, 64.3, 0, 0.5), 1E-9);
        assertEquals(37.6, Interpolation.linear2DWithFade(0.3, -7, 10.9, 64.3, 1, 0.5), 1E-9);
        assertEquals(5.6, Interpolation.linear2DWithFade(0.3, -7, 10.9, 64.3, 0.5, 0), 1E-9);
        assertEquals(28.65, Interpolation.linear2DWithFade(0.3, -7, 10.9, 64.3, 0.5, 1), 1E-9);
        assertEquals(2.45248574448, Interpolation.linear2DWithFade(0.3, -7, 10.9, 64.3, 0.3, 0.3), 1E-9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DIllegalMuX() {
        double x = 0.0;
        Interpolation.linear3D(x, x, x, x, x, x, x, x, -1, 0.2, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DIllegalMuX2() {
        double x = 0.0;
        Interpolation.linear3D(x, x, x, x, x, x, x, x, 4, 0.2, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DIllegalMuY() {
        double x = 0.0;
        Interpolation.linear3D(x, x, x, x, x, x, x, x, 0.1, -6, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DIllegalMuY2() {
        double x = 0.0;
        Interpolation.linear3D(x, x, x, x, x, x, x, x, 0.1, 2, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DIllegalMuZ() {
        double x = 0.0;
        Interpolation.linear3D(x, x, x, x, x, x, x, x, 0.1, 0.2, -8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DIllegalMuZ2() {
        double x = 0.0;
        Interpolation.linear3D(x, x, x, x, x, x, x, x, 0.1, 0.2, 5);
    }

    @Test
    public void test3DTrivialInZ() {
        double expected = 0.5;
        double actual = Interpolation.linear3D(0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.5, 0.5, 0.5);
        assertEquals(expected, actual, 1E-9);
    }

    @Test
    public void test3DTrivialInX() {
        double expected = 0.5;
        double actual = Interpolation.linear3D(0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.5, 0.5, 0.5);
        assertEquals(expected, actual, 1E-9);
    }

    @Test
    public void test3DTrivialInY() {
        double expected = 0.5;
        double actual = Interpolation.linear3D(0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.5, 0.5, 0.5);
        assertEquals(expected, actual, 1E-9);
    }

    @Test
    public void test3DNonTrivial() {
        double expected = 1.1;
        double actual = Interpolation.linear3D(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.1, 0.2, 0.3);
        assertEquals(expected, actual, 1E-9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DWithFadeIllegalMuX() {
        double x = 0.0;
        Interpolation.linear3DWithFade(x, x, x, x, x, x, x, x, -1, 0.2, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DWithFadeIllegalMuX2() {
        double x = 0.0;
        Interpolation.linear3DWithFade(x, x, x, x, x, x, x, x, 4, 0.2, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DWithFadeIllegalMuY() {
        double x = 0.0;
        Interpolation.linear3DWithFade(x, x, x, x, x, x, x, x, 0.1, -6, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DWithFadeIllegalMuY2() {
        double x = 0.0;
        Interpolation.linear3DWithFade(x, x, x, x, x, x, x, x, 0.1, 2, 0.7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DWithFadeIllegalMuZ() {
        double x = 0.0;
        Interpolation.linear3DWithFade(x, x, x, x, x, x, x, x, 0.1, 0.2, -8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test3DWithFadeIllegalMuZ2() {
        double x = 0.0;
        Interpolation.linear3DWithFade(x, x, x, x, x, x, x, x, 0.1, 0.2, 5);
    }

    @Test
    public void test3DWithFadeNonTrivial() {
        double expected = 0.31316;
        double actual = Interpolation.linear3DWithFade(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 0.1, 0.2, 0.3);
        assertEquals(expected, actual, 1E-9);
    }
}