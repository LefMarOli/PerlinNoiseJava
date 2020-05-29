package org.lefmaroli.perlin.line;

import org.junit.Test;
import org.lefmaroli.vector.Vector2D;

import static org.junit.Assert.assertEquals;

public class DistanceMapper2DTest {


    @Test(expected = IllegalArgumentException.class)
    public void testInvalidXLength() {
        new DistanceMapper2D(-5, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidXLength2() {
        new DistanceMapper2D(0, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidYLength() {
        new DistanceMapper2D(6, -5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidYLength2() {
        new DistanceMapper2D(6, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidXInquiry(){
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(50, 50);
        distanceMapper2D.getForCoordinates(-5, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidXInquiry2(){
        int xLength = 60;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(xLength, 50);
        distanceMapper2D.getForCoordinates(xLength, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidXInquiry3(){
        int xLength = 60;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(xLength, 50);
        distanceMapper2D.getForCoordinates(xLength + 10, 20);
    }

    @Test
    public void testXInquiry(){
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(50, 50);
        distanceMapper2D.getForCoordinates(0, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidYInquiry(){
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(50, 50);
        distanceMapper2D.getForCoordinates(20, -6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidYInquiry2(){
        int yLength = 64;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(50, yLength);
        distanceMapper2D.getForCoordinates(20, yLength);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidYInquiry3(){
        int yLength = 64;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(50, yLength);
        distanceMapper2D.getForCoordinates(20, yLength + 10);
    }

    @Test
    public void testYInquiry(){
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(50, 50);
        distanceMapper2D.getForCoordinates(20, 0);
    }

    @Test
    public void testTopLeftDistanceVector() {
        int xLength = 50;
        int yLength = 30;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(xLength, yLength);
        int x = 5;
        int y = 26;
        Vector2D topLeft = distanceMapper2D.getForCoordinates(x, y).getTopLeftDistance();
        assertEquals(x / (double) xLength, topLeft.getX(), 1E-9);
        assertEquals(y / (double) yLength, topLeft.getY(), 1E-9);
    }

    @Test
    public void testTopRightDistanceVector() {
        int xLength = 50;
        int yLength = 30;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(xLength, yLength);
        int x = 5;
        int y = 26;
        Vector2D topRight = distanceMapper2D.getForCoordinates(x, y).getTopRightDistance();
        assertEquals(x / (double) xLength - 1.0, topRight.getX(), 1E-9);
        assertEquals(y / (double) yLength, topRight.getY(), 1E-9);
    }

    @Test
    public void testBottomLeftDistanceVector() {
        int xLength = 50;
        int yLength = 30;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(xLength, yLength);
        int x = 5;
        int y = 26;
        Vector2D bottomLeft = distanceMapper2D.getForCoordinates(x, y).getBottomLeftDistance();
        assertEquals(x / (double) xLength, bottomLeft.getX(), 1E-9);
        assertEquals(y / (double) yLength - 1.0, bottomLeft.getY(), 1E-9);
    }

    @Test
    public void testBottomRightDistanceVector() {
        int xLength = 50;
        int yLength = 30;
        DistanceMapper2D distanceMapper2D = new DistanceMapper2D(xLength, yLength);
        int x = 5;
        int y = 26;
        Vector2D bottomRight = distanceMapper2D.getForCoordinates(x, y).getBottomRightDistance();
        assertEquals(x / (double) xLength - 1.0, bottomRight.getX(), 1E-9);
        assertEquals(y / (double) yLength - 1.0, bottomRight.getY(), 1E-9);
    }

}