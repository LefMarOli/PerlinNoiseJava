package org.lefmaroli.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class SimpleGrayScaleImage {

  private static final Color[] COLORS = new Color[256];

  static {
    for (var i = 0; i <= 255; i++) {
      COLORS[i] = new Color(i, i, i);
    }
  }

  private final BufferedImage image;
  private final int pixelScale;
  private final JFrame framedImage;
  private final JLabel label;
  private final int[][] colors;
  private final int width;
  private final int length;

  public SimpleGrayScaleImage(double[][] data, int pixelScale) {
    assertDataIsRectangular(data);
    this.width = data.length;
    this.length = data[0].length;
    this.pixelScale = pixelScale;
    this.colors = new int[width][length];
    image = new BufferedImage(width, length, BufferedImage.TYPE_BYTE_GRAY);
    this.label = new JLabel();
    framedImage = initializeImageFrame(label);
    updateImage(data);
  }

  private static void assertDataIsRectangular(double[][] data) {
    if (data.length < 1) {
      throw new IllegalArgumentException("Provided data is empty");
    }
    int rowLength = data[0].length;
    for (double[] row : data) {
      if (row.length != rowLength) {
        throw new IllegalArgumentException("Provided data doesn't have same length across rows");
      }
    }
  }

  public void setVisible() {
    this.framedImage.setVisible(true);
  }

  public void updateImage(double[][] newData) {
    assertNewDataHasSameDimensions(newData);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < length; j++) {
        int colorIndex = (int) (newData[i][j] * 255);
        colors[i][j] = colorIndex;
      }
    }
    SwingUtilities.invokeLater(
        () -> {
          Graphics2D g = (Graphics2D) image.getGraphics();
          for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
              g.setColor(COLORS[colors[i][j]]);
              g.fillRect(i, j, pixelScale, pixelScale);
            }
          }
          label.setIcon(new ImageIcon(image));
          framedImage.pack();
        });
  }

  public void dispose() {
    framedImage.dispose();
  }

  private void assertNewDataHasSameDimensions(double[][] data) {
    if (data.length != width) {
      throw new IllegalArgumentException("Provided data has changed width");
    }
    for (double[] row : data) {
      if (row.length != length) {
        throw new IllegalArgumentException(
            "Provided data doesn't have same length as original data");
      }
    }
  }

  private JFrame initializeImageFrame(JLabel label) {
    JFrame frame = new JFrame();
    frame.setSize(image.getWidth(), image.getHeight());
    frame.getContentPane().add(label, BorderLayout.CENTER);
    frame.setLocationRelativeTo(null);
    return frame;
  }
}
