package org.lefmaroli.perlin;

import java.awt.EventQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.display.SimpleGrayScaleImage;

public class PerlinNoiseTest {

  @Test
  public void test1D() {

    int size = 1024;
    double[] values = new double[size];
    double stepSize = 0.05;
    for (int i = 0; i < values.length; i++) {
      values[i] = PerlinNoise.perlin(i * stepSize);
    }

    LineChart chart = new LineChart("Test", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(values, label);
    chart.setVisible();
    chart.setYAxisRange(-1.0, 1.0);
    while (true) ;
  }

  @Test
  public void test1D_2() {

    int size = 1024;
    double[] values = new double[size];
    double stepSize = 0.05;
    for (int i = 0; i < values.length; i++) {
      values[i] = PerlinNoise.perlin(i * stepSize, 0);
    }

    LineChart chart = new LineChart("Test", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(values, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);
    while (true) ;
  }

  @Test
  public void test2D() throws InterruptedException {

    int size = 500;
    double[] values = new double[size];
    double stepSizeX = 0.01;
    double stepSizeY = 0.055;
    for (int i = 0; i < size; i++) {
        values[i] = PerlinNoise.perlin(0 * stepSizeX, i * stepSizeY, 0);
    }
    int currentXIndex = 0;

    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for (double v : values) {
      if (v < min) {
        min = v;
      }
      if (v > max) {
        max = v;
      }
    }

    System.out.println("Min: " + min + ", Max: " + max);

    LineChart chart = new LineChart("Test", "length", "values");
    String label = "line";
    chart.addEquidistantDataSeries(values, label);
    chart.setVisible();
    chart.setYAxisRange(0.0, 1.0);
    long previousTime = System.currentTimeMillis();
    while (true) {
      if (System.currentTimeMillis() - previousTime > 2) {
        previousTime = System.currentTimeMillis();
        currentXIndex++;
        for (int i = 0; i < size; i++) {
          double newValue = PerlinNoise.perlin(currentXIndex * stepSizeX, i * stepSizeY, 0);
          values[i] = newValue;
          if(newValue > max){
            max = newValue;
            System.out.println("New max: " + max);
          }
          if(newValue < min){
            min = newValue;
            System.out.println("New min: " + min);
          }
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

    final int size = 500;
    double[][] values = new double[size][size];
    double stepSize = 0.01;
    double stepSizeY = 0.01;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        values[i][j] = PerlinNoise.perlin(i * stepSize, j * stepSizeY, 0);
      }
    }
    SimpleGrayScaleImage image = new SimpleGrayScaleImage(values, 5);
    image.setVisible();
    AtomicInteger currentIndex = new AtomicInteger(size -1);
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.scheduleAtFixedRate(() -> {
      for (int i = 1; i < size; i++) {
        System.arraycopy(values[i], 0, values[i - 1], 0, size);
      }
      int index = currentIndex.incrementAndGet();
      for (int i = 0; i < size; i++) {
        values[size - 1][i] = PerlinNoise.perlin(index * stepSize, i * stepSizeY, 0);
      }
      image.updateImage(values);
    }, 5, 5, TimeUnit.MILLISECONDS);

    while(true){
      try {
        Thread.sleep(50000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Test
  public void getNextSlice() throws InterruptedException {

    final int size = 500;
    double[][] values = new double[size][size];
    double stepSize = 0.2;
    double stepSizeY = 0.01;
    double stepSizeZ = 0.05;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        values[i][j] = PerlinNoise.perlin(i * stepSize, j * stepSizeY, 0);
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
            values[i][j] = PerlinNoise.perlin(i * stepSize, j * stepSizeY, zIndex * stepSizeZ);
            }
          }
          image.updateImage(values);
        },50
        ,50
        ,
        TimeUnit.MILLISECONDS);

    while(true){
      Thread.sleep(500000);
    }
  }
}