package org.lefmaroli.perlin;

import static org.awaitility.Awaitility.waitAtMost;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;
import org.lefmaroli.perlin.line.LineNoiseGenerator;
import org.lefmaroli.perlin.line.LineNoiseGeneratorBuilder;
import org.lefmaroli.perlin.point.PointNoiseGenerator;
import org.lefmaroli.perlin.point.PointNoiseGeneratorBuilder;
import org.lefmaroli.perlin.slice.SliceNoiseGenerator;
import org.lefmaroli.perlin.slice.SliceNoiseGeneratorBuilder;

public class PerlinNoisePerformanceTest {

  private PerlinNoise perlinNoise;
  Logger logger = LogManager.getLogger(PerlinNoisePerformanceTest.class);

  @Before
  public void setup() {
    perlinNoise = new PerlinNoise(1, System.currentTimeMillis());
  }

  @Test
  public void testPerformance() {
    ExecutorService service = Executors.newSingleThreadExecutor();
    AtomicBoolean isDone = new AtomicBoolean(false);
    service.submit(
        () -> {
          int numIterations = 1000000;
          long start = System.currentTimeMillis();
          for (int i = 0; i < numIterations; i++) {
            perlinNoise.getFor(i * 0.005);
          }
          logger.info("Done in " + (System.currentTimeMillis() - start) + "ms");
          isDone.set(true);
        });
    try {
      Awaitility.setDefaultPollInterval(Duration.ofMillis(10));
      waitAtMost(Duration.ofMillis(80)).until(isDone::get);
    } finally {
      service.shutdown();
      try {
        boolean areTasksDone = service.awaitTermination(10, TimeUnit.SECONDS);
        if(!areTasksDone){
          logger.error("Tasks were not completed within the delay");
        }
      } catch (InterruptedException e) {
        logger.error("Awaiting task termination was interrupted");
        Thread.currentThread().interrupt();
      }
    }
  }

  @Test
  public void benchmarkPointGeneratorPerformance() throws NoiseBuilderException {
    PointNoiseGenerator noiseGenerator =
        new PointNoiseGeneratorBuilder()
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();

    ExecutorService service = Executors.newSingleThreadExecutor();
    AtomicBoolean isDone = new AtomicBoolean(false);
    service.submit(
        () -> {
          int numberOfIterations = 50000;
          long start = System.currentTimeMillis();
          for (int i = 0; i < numberOfIterations; i++) {
            noiseGenerator.getNext();
          }
          logger.info("Done in " + (System.currentTimeMillis() - start) + "ms");
          isDone.set(true);
        });
    try {
      Awaitility.setDefaultPollInterval(Duration.ofMillis(10));
      waitAtMost(Duration.ofMillis(170)).until(isDone::get);
    } finally {
      service.shutdown();
      try {
        boolean areTasksDone = service.awaitTermination(10, TimeUnit.SECONDS);
        if(!areTasksDone){
          logger.error("Tasks were not completed within the delay");
        }
      } catch (InterruptedException e) {
        logger.error("Awaiting task termination was interrupted");
        Thread.currentThread().interrupt();
      }
    }
  }

  @Test
  public void benchmarkLineGeneratorPerformance() throws NoiseBuilderException {
    LineNoiseGenerator noiseGenerator =
        new LineNoiseGeneratorBuilder(1000)
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withLineStepSizeGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();

    ExecutorService service = Executors.newSingleThreadExecutor();
    AtomicBoolean isDone = new AtomicBoolean(false);
    service.submit(
        () -> {
          int numberOfIterations = 500;
          long start = System.currentTimeMillis();
          for (int i = 0; i < numberOfIterations; i++) {
            noiseGenerator.getNext();
          }
          logger.info("Done in " + (System.currentTimeMillis() - start) + "ms");
          isDone.set(true);
        });
    try {
      Awaitility.setDefaultPollInterval(Duration.ofMillis(10));
      waitAtMost(Duration.ofMillis(700)).until(isDone::get);
    } finally {
      service.shutdown();
      try {
        boolean areTasksDone = service.awaitTermination(10, TimeUnit.SECONDS);
        if(!areTasksDone){
          logger.error("Tasks were not completed within the delay");
        }
      } catch (InterruptedException e) {
        logger.error("Awaiting task termination was interrupted");
        Thread.currentThread().interrupt();
      }
    }
  }

  @Test
  public void benchmarkSliceGeneratorPerformance() throws NoiseBuilderException {
    SliceNoiseGenerator noiseGenerator =
        new SliceNoiseGeneratorBuilder(400, 400)
            .withNumberOfLayers(10)
            .withRandomSeed(0L)
            .withNoiseStepSizeGenerator(new DoubleGenerator(1.0 / 1000, 2.0))
            .withWidthInterpolationPointGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withHeightInterpolationPointGenerator(new DoubleGenerator(1.0 / 50, 0.5))
            .withAmplitudeGenerator(new DoubleGenerator(1.0, 0.85))
            .build();

    ExecutorService service = Executors.newSingleThreadExecutor();
    AtomicBoolean isDone = new AtomicBoolean(false);
    service.submit(
        () -> {
          int numberOfIterations = 5;
          long start = System.currentTimeMillis();
          for (int i = 0; i < numberOfIterations; i++) {
            noiseGenerator.getNext();
          }
          logger.info("Done in " + (System.currentTimeMillis() - start) + "ms");
          isDone.set(true);
        });
    try {
      Awaitility.setDefaultPollInterval(Duration.ofMillis(10));
      waitAtMost(Duration.ofMillis(2200)).until(isDone::get);
    } finally {
      service.shutdown();
      try {
        boolean areTasksDone = service.awaitTermination(100, TimeUnit.SECONDS);
        if(!areTasksDone){
          logger.error("Tasks were not completed within the delay");
        }
      } catch (InterruptedException e) {
        logger.error("Awaiting task termination was interrupted");
        Thread.currentThread().interrupt();
      }
    }
  }
}
