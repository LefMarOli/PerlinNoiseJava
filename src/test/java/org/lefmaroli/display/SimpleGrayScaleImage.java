package org.lefmaroli.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class SimpleGrayScaleImage {

  private static final Color[] COLORS = new Color[256];

  static {
    for (int i = 0; i <= 255; i++) {
      COLORS[i] = new Color(i, i, i);
    }
  }

  private final BufferedImage image;
  private final int pixelScale;
  private final JFrame framedImage;
  private final JLabel label;
  private final int width;
  private final int length;

  public SimpleGrayScaleImage(double[][] data, int pixelScale) {
    assertDataIsRectangular(data);
    this.width = data.length;
    this.length = data[0].length;
    this.pixelScale = pixelScale;
    image = new BufferedImage(width, length, BufferedImage.TYPE_BYTE_GRAY);
    this.label = new JLabel();
    framedImage = initializeImageFrame(label);
    updateImage(data);
    framedImage.pack();
  }

  public void setVisible() {
    this.framedImage.setVisible(true);
  }

  public void updateImage(double[][] newData) {
    assertNewDataHasSameDimensions(newData);
    Graphics2D g = (Graphics2D) image.getGraphics();
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < length; j++) {
        g.setColor(COLORS[(int) (newData[i][j] * 255)]);
        g.fillRect(i, j, pixelScale, pixelScale);
      }
    }

    EventQueue.invokeLater(
        () -> {
          label.setIcon(new ImageIcon(image));
          framedImage.revalidate();
          framedImage.repaint();
        });
  }

  private void assertDataIsRectangular(double[][] data) {
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
