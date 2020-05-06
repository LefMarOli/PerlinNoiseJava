package org.lefmaroli.perlin2d;

import org.junit.Ignore;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Vector;

import static org.junit.Assert.*;

public class PerlinLayer2DTest {

    @Test
    public void getNextSlicesCorrectSize() {
        int width = 500;
        PerlinLayer2D layer2D = new PerlinLayer2D(width, 200, 1.0, System.currentTimeMillis());

        int requestedDataPoints = 500;
        Vector<Vector<Double>> nextSlices = layer2D.getNextXSlices(requestedDataPoints);
        assertEquals(requestedDataPoints, nextSlices.size(), 0);
        for (Vector<Double> nextSlice : nextSlices) {
            assertEquals(width, nextSlice.size(), 0);
        }
    }

    @Test
    public void testValuesBounded() {
        int width = 500;
        PerlinLayer2D layer2D = new PerlinLayer2D(width, 200, 1.0, System.currentTimeMillis());

        int requestedDataPoints = 500;
        Vector<Vector<Double>> nextSlices = layer2D.getNextXSlices(requestedDataPoints);

        for (Vector<Double> nextSlice : nextSlices) {
            for (Double value : nextSlice) {
                assertNotNull(value);
                assertTrue(value > 0.0);
                assertTrue(value < 1.0);
            }
        }
    }

    @Test
    public void testValuesMultipliedByAmplitudeFactor() {
        int width = 500;
        long randomSeed = System.currentTimeMillis();
        PerlinLayer2D layer = new PerlinLayer2D(width, 50, 1.0, randomSeed);
        Random random = new Random(System.currentTimeMillis());
        double amplitudeFactor = random.nextDouble() * 100;
        PerlinLayer2D amplifiedLayer = new PerlinLayer2D(width, 50, amplitudeFactor, randomSeed);

        int count = 500;
        Vector<Vector<Double>> values = layer.getNextXSlices(count);
        Vector<Vector<Double>> actualAmplifiedValues = amplifiedLayer.getNextXSlices(count);

        for (Vector<Double> xSlice : values) {
            for (int j = 0; j < xSlice.size(); j++) {
                xSlice.set(j, xSlice.get(j) * amplitudeFactor);
            }
        }

        for (int i = 0; i < values.size(); i++) {
            assertExpectedVectorEqualsActual(values.get(i), actualAmplifiedValues.get(i), 1e-18);
        }
    }

    @Test
    public void testCreateSame() {
        int width = 500;
        long randomSeed = System.currentTimeMillis();
        PerlinLayer2D layer = new PerlinLayer2D(width, 50, 1.0, randomSeed);
        PerlinLayer2D sameLayer = new PerlinLayer2D(width, 50, 1.0, randomSeed);
        int expectedCount = 75;
        Vector<Vector<Double>> nextSegment1 = layer.getNextXSlices(expectedCount);
        Vector<Vector<Double>> nextSegment2 = sameLayer.getNextXSlices(expectedCount);

        assertEquals(nextSegment1.size(), nextSegment2.size(), 0);
        for (int i = 0; i < nextSegment1.size(); i++) {
            assertExpectedVectorEqualsActual(nextSegment1.get(i), nextSegment2.get(i), 0.0);
        }
    }

    private void assertExpectedVectorEqualsActual(Vector<Double> expected, Vector<Double> actual, double delta) {
        assertEquals(expected.size(), actual.size(), delta);
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i), delta);
        }
    }

    @Ignore
    @Test
    public void getNextSlices() {

        int width = 500;
        PerlinLayer2D layer2D = new PerlinLayer2D(width, 200, 1.0, System.currentTimeMillis());

        int requestedDataPoints = 500;
        Vector<Vector<Double>> nextSlices = layer2D.getNextXSlices(requestedDataPoints);

        assertEquals(width, nextSlices.size(), 0);

        for (Vector<Double> slice : nextSlices) {
            assertEquals(requestedDataPoints, slice.size(), 0);
        }

//        XYSeriesCollection dataset = LineChart.createEquidistantDataset(nextSlices.get(0), "Perlin2Dy");
//        EventQueue.invokeLater(() -> {
//            JFrame framedChart = LineChart.getFramedChart("Perlin2Dy", "Sequence", "Value", dataset);
//            framedChart.setVisible(true);
//        });
//
//        Vector<Double> widthSlice = new Vector<>();
//        for (Vector<Double> slice : nextSlices) {
//            widthSlice.add(slice.get(0));
//        }
//
//        XYSeriesCollection dataset2 = LineChart.createEquidistantDataset(widthSlice, "Perlin2Dx");
//        EventQueue.invokeLater(() -> {
//            JFrame framedChart = LineChart.getFramedChart("Perlin2Dx", "Sequence", "Value", dataset2);
//            framedChart.setVisible(true);
//        });

//        while(true);

        Color[] colors = new Color[256];

        for (int i = 0; i <= 255; i++) {
            colors[i] = new Color(i, i, i);
        }

        final BufferedImage img = new BufferedImage(requestedDataPoints, width, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = (Graphics2D) img.getGraphics();
        for (int i = 0; i < requestedDataPoints; i++) {
            for (int j = 0; j < width; j++) {
                g.setColor(colors[(int) ((nextSlices.get(j).get(i) + 1.0) / 2.0 * 255)]);
                g.fillRect(i, j, 5, 5);
            }
        }

        JFrame frame = new JFrame();
        frame.setSize(img.getWidth(), img.getHeight());
        JLabel label = new JLabel();
        label.setIcon(new ImageIcon(img));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        long previousTime = System.currentTimeMillis();
        while (true) {
            if (System.currentTimeMillis() - previousTime > 5) {
                previousTime = System.currentTimeMillis();
                nextSlices.add(layer2D.getNextXSlices(1).get(0));
                nextSlices.remove(0);
                final BufferedImage newImage =
                        new BufferedImage(requestedDataPoints, width, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D newGraphics = (Graphics2D) newImage.getGraphics();
                for (int i = 0; i < requestedDataPoints; i++) {
                    for (int j = 0; j < width; j++) {
//                        newGraphics.setColor(colors[(int) ((nextSlices.get(i).get(j) + 1.0) / 2.0 * 255)]);
                        newGraphics.setColor(colors[(int) ((nextSlices.get(i).get(j)) * 255)]);
                        newGraphics.fillRect(i, j, 5, 5);
                    }
                }
                EventQueue.invokeLater(() -> {
                    label.setIcon(new ImageIcon(newImage));
                    frame.revalidate();
                    frame.repaint();
                });
            }
        }
    }
}