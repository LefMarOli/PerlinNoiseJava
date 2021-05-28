package org.lefmaroli.perlin.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.lefmaroli.display.LineChart;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.perlin.generators.LayeredGeneratorBuilderException;
import org.lefmaroli.perlin.generators.LayeredPointGenerator;
import org.lefmaroli.perlin.generators.LayeredPointGeneratorBuilder;
import org.lefmaroli.testutils.AssertUtils;
import org.lefmaroli.testutils.ScheduledUpdater;

class LayeredPointGeneratorTest {

  private static final double maxAmplitude = 1.75;
  private static LayeredPointGenerator defaultGenerator;
  private static LayeredPointGeneratorBuilder defaultBuilder;
  private static final int numLayers = 3;
  private static final long randomSeed = System.currentTimeMillis();

  @BeforeAll
  static void init() throws LayeredGeneratorBuilderException {
    defaultBuilder = new LayeredPointGeneratorBuilder();
    resetBuilder(defaultBuilder);
    defaultGenerator = defaultBuilder.build();
  }

  private static LayeredPointGeneratorBuilder resetBuilder(LayeredPointGeneratorBuilder builder) {
    return builder
        .withRandomSeed(randomSeed)
        .withNumberOfLayers(numLayers)
        .withAmplitudes(List.of(1.0, 0.5, 0.25))
        .withNoiseStepSizes(List.of(1 / 2048.0, 1 / 1024.0, 1 / 512.0))
        .withLayerExecutorService(null);
  }

  @BeforeEach
  void setup() {
    resetBuilder(defaultBuilder);
  }

  @Test
  void testNotNull() throws LayeredGeneratorBuilderException {
    Assertions.assertNotNull(defaultBuilder.build());
  }

  @ParameterizedTest
  @MethodSource("invalidStepSizes")
  void testInvalidNoiseStepSizes(List<Double> stepSizes) {
    defaultBuilder.withNoiseStepSizes(stepSizes);
    Assertions.assertThrows(LayeredGeneratorBuilderException.class, () -> defaultBuilder.build());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> invalidStepSizes() {
    return Stream.of(Arguments.of(List.of(1.0, -5.0, 9.0)), Arguments.of(List.of(0.2, 0.5, 0.0)));
  }

  @Test
  void notEnoughNoiseStepSizes() {
    defaultBuilder.withNoiseStepSizes(List.of(0.2, 0.5));
    Assertions.assertThrows(LayeredGeneratorBuilderException.class, () -> defaultBuilder.build());
  }

  @Test
  void testDimension() {
    assertEquals(1, defaultGenerator.getDimensions());
  }

  @Test
  void testGetTotalSize() {
    assertEquals(numLayers, defaultGenerator.getTotalSize());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1, 115})
  void testCreateWrongNumberOfLayers(int numberOfLayers) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withNumberOfLayers(numberOfLayers));
  }

  @Test
  void testGetNextBoundedValues() {
    for (int i = 0; i < 10000; i++) {
      Double pointNoiseData = defaultGenerator.getNext();
      Assertions.assertTrue(pointNoiseData <= 1.0);
      Assertions.assertTrue(pointNoiseData >= 0.0);
    }
  }

  @Test
  void testGetMaxAmplitude() {
    assertEquals(maxAmplitude, defaultGenerator.getMaxAmplitude(), 0.0);
  }

  @Test
  void testNumLayersGenerated() {
    assertEquals(numLayers, defaultGenerator.getNumberOfLayers(), 0);
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testEqualsArgs")
  @SuppressWarnings("unused")
  void testEquals(Object first, Object second, String title) {
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testEqualsArgs() throws LayeredGeneratorBuilderException {
    LayeredPointGeneratorBuilder other = resetBuilder(new LayeredPointGeneratorBuilder());
    defaultGenerator = resetBuilder(defaultBuilder).build();
    return Stream.of(
        Arguments.of(
            defaultGenerator, defaultBuilder.build(), "Different generators from same builder"),
        Arguments.of(
            defaultGenerator, other.build(), "Different generators from different builders"),
        Arguments.of(defaultGenerator, defaultGenerator, "Same generator"));
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("testNotEqualsArgs")
  @SuppressWarnings("unused")
  void testNotEquals(Object other, String title) {
    Assertions.assertNotEquals(defaultGenerator, other);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testNotEqualsArgs() throws LayeredGeneratorBuilderException {
    LayeredPointGeneratorBuilder other = resetBuilder(new LayeredPointGeneratorBuilder());
    return Stream.of(
        Arguments.of(
            resetBuilder(defaultBuilder).withNumberOfLayers(numLayers - 1).build(),
            "Different number of layers"),
        Arguments.of(
            resetBuilder(defaultBuilder).withAmplitudes(List.of(1.0, 5.0, 6.0)).build(),
            "Different amplitudes"),
        Arguments.of(
            resetBuilder(defaultBuilder).withRandomSeed(randomSeed + 1).build(),
            "Different random seed"),
        Arguments.of(
            resetBuilder(defaultBuilder).withNoiseStepSizes(List.of(0.2, 0.02, 0.002)).build(),
            "Different noise step sizes"),
        Arguments.of(null, "null"),
        Arguments.of(new Random(), "object from different class"));
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(defaultGenerator.getClass())
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "scheduler",
            "jitterStrategy",
            "logger",
            "containers",
            "generated",
            "containersCount",
            "futures",
            "totalSize",
            "timeout",
            "executorService",
            "emittedExecutorShutdownWarning")
        .verify();
  }

  @Test
  void testSmoothVisuals() throws LayeredGeneratorBuilderException { // NOSONAR
    DoubleGenerator noiseStepSizeGenerator = new DoubleGenerator(1.0 / 200, 2.0);
    DoubleGenerator amplitudeGenerator = new DoubleGenerator(1.0, 0.95);
    int numLayers = 4;
    LayeredPointGenerator generator =
        new LayeredPointGeneratorBuilder()
            .withNoiseStepSizes(noiseStepSizeGenerator)
            .withNumberOfLayers(numLayers)
            .withAmplitudes(amplitudeGenerator)
            .build();
    int requestedPoints = 200;
    final double[] line = new double[requestedPoints];
    for (int i = 0; i < requestedPoints; i++) {
      line[i] = generator.getNext();
    }

    AtomicReference<LineChart> chart = new AtomicReference<>();
    boolean isDisplaySupported = !GraphicsEnvironment.isHeadless();
    String label = "line";
    if (isDisplaySupported) {
      LineChart c = new LineChart("Test", "length", "values");
      c.addEquidistantDataSeries(line, label);
      c.setVisible();
      c.setYAxisRange(0.0, 1.0);
      chart.set(c);
    }

    int[] placeholder = new int[requestedPoints - 1];
    CompletableFuture<Void> completed =
        ScheduledUpdater.updateAtRateForDuration(
            () -> {
              System.arraycopy(line, 1, line, 0, requestedPoints - 1);
              if (Thread.interrupted()) {
                return;
              }
              line[requestedPoints - 1] = generator.getNext();
              if (isDisplaySupported) {
                SwingUtilities.invokeLater(
                    () ->
                        chart
                            .get()
                            .updateDataSeries(
                                dataSeries -> {
                                  for (int i = 0; i < line.length; i++) {
                                    dataSeries.updateByIndex(i, line[i]);
                                  }
                                },
                                label));
              }
              try {
                AssertUtils.valuesContinuousInArray(line, placeholder);
              } catch (AssertionError e) {
                LogManager.getLogger(this.getClass())
                    .error("Error with line smoothness for point generator " + generator, e);
                throw e;
              }
            },
            30,
            TimeUnit.MILLISECONDS,
            5,
            TimeUnit.SECONDS);
    completed.thenRun(
        () -> {
          if (isDisplaySupported) chart.get().dispose();
        });
  }
}
