package org.lefmaroli.perlin;

import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;

@Ignore("Tests skipped, visual assessment only")
public class PerlinNoiseVisualizeTests {

  private static final Logger LOGGER = LogManager.getLogger(PerlinNoiseVisualizeTests.class);
  private final long randomSeed = System.currentTimeMillis();

  @Test
  public void test2D() throws InterruptedException {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    int size = 500;
    double[] values = new double[size];
    double stepSizeX = 0.01;
    double stepSizeY = 0.055;
    for (int i = 0; i < size; i++) {
      values[i] = perlinNoise.getFor(0 * stepSizeX, i * stepSizeY);
    }
    int currentXIndex = 0;

    LineChart chart = new LineChart("Test", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(values, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);

    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 5) {
        previousTime = System.currentTimeMillis();
        currentXIndex++;
        for (int i = 0; i < size; i++) {
          double newValue = perlinNoise.getFor(currentXIndex * stepSizeX, i * stepSizeY);
          values[i] = newValue;
        }
        EventQueue.invokeLater(
            () ->
                chart.updateDataSeries(
                    dataSeries -> {
                      for (int i = 0; i < values.length; i++) {
                        dataSeries.updateByIndex(i, values[i]);
                      }
                    },
                    label));
      } else {
        Thread.sleep(2);
      }
    }
  }

  @Test
  public void getNextLines() {
    PerlinNoise perlinNoise = new PerlinNoise(2, randomSeed);
    final int size = 500;
    double[][] values = new double[size][size];
    double stepSize = 0.01;
    double stepSizeY = 0.01;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        values[i][j] = perlinNoise.getFor(i * stepSize, j * stepSizeY);
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(values, 5);
    image.setVisible();
    AtomicInteger currentIndex = new AtomicInteger(size - 1);
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.scheduleAtFixedRate(
        () -> {
          for (int i = 1; i < size; i++) {
            System.arraycopy(values[i], 0, values[i - 1], 0, size);
          }
          int index = currentIndex.incrementAndGet();
          for (int i = 0; i < size; i++) {
            values[size - 1][i] = perlinNoise.getFor(index * stepSize, i * stepSizeY);
          }
          image.updateImage(values);
        },
        5,
        5,
        TimeUnit.MILLISECONDS);

    while (true) {
      try {
        Thread.sleep(50000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void getNextSlice() throws InterruptedException {
    PerlinNoise perlinNoise = new PerlinNoise(3, randomSeed);
    final int size = 500;
    double[][] values = new double[size][size];
    double stepSize = 0.01;
    double stepSizeY = 0.01;
    double stepSizeZ = 0.05;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        values[i][j] = perlinNoise.getFor(i * stepSize, j * stepSizeY, 0);
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(values, 5);
    image.setVisible();
    AtomicInteger currentZIndex = new AtomicInteger(0);
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.scheduleAtFixedRate(
        () -> {
          int zIndex;
          zIndex = currentZIndex.incrementAndGet();
          for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
              values[i][j] = perlinNoise.getFor(i * stepSize, j * stepSizeY, zIndex * stepSizeZ);
            }
          }
          image.updateImage(values);
        },
        15,
        15,
        TimeUnit.MILLISECONDS);

    while (true) {
      Thread.sleep(500000);
    }
  }
}
