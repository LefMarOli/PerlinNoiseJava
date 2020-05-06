package org.lefmaroli.perlin2d;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

import static org.junit.Assert.assertEquals;

class PerlinLayer2DTest {

    @Test
    void getNextSlices() {

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
                final BufferedImage newImage = new BufferedImage(requestedDataPoints, width, BufferedImage.TYPE_BYTE_GRAY);
                Graphics2D newGraphics = (Graphics2D) newImage.getGraphics();
                for (int i = 0; i < requestedDataPoints; i++) {
                    for (int j = 0; j < width; j++) {
                        newGraphics.setColor(colors[(int) ((nextSlices.get(i).get(j) + 1.0) / 2.0 * 255)]);
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