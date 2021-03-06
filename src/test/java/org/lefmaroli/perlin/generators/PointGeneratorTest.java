package org.lefmaroli.perlin.generators;

import com.jparams.verifier.tostring.NameStyle;
import com.jparams.verifier.tostring.ToStringVerifier;
import com.jparams.verifier.tostring.preset.Presets;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class PointGeneratorTest {

  private static final double noiseStepSize = 1.0 / 51;
  private static final double amplitude = 1.0;
  private static final int expectedCount = 500;
  private static final long randomSeed = System.currentTimeMillis();
  private static PointGenerator defaultGenerator;
  private static PointGeneratorBuilder defaultBuilder;

  @BeforeAll
  static void init() throws StepSizeException {
    defaultBuilder = new PointGeneratorBuilder();
    resetBuilder(defaultBuilder);
    defaultGenerator = defaultBuilder.build();
  }

  private static PointGeneratorBuilder resetBuilder(PointGeneratorBuilder builder)
      throws StepSizeException {
    builder.withTimeStepSize(noiseStepSize).withAmplitude(amplitude).withRandomSeed(randomSeed);
    return builder;
  }

  @BeforeEach
  void setup() throws StepSizeException {
    resetBuilder(defaultBuilder);
  }

  @Test
  void testDimension() {
    Assertions.assertEquals(1, defaultGenerator.getDimensions());
  }

  @Test
  void testValuesBounded() {
    for (int i = 0; i < 1000000; i++) {
      Double pointNoiseData = defaultGenerator.getNext();
      Assertions.assertNotNull(pointNoiseData);
      Assertions.assertTrue(pointNoiseData < 1.0, "Value is greater than 1.0:" + pointNoiseData);
      Assertions.assertTrue(pointNoiseData > 0.0, "Value is lower than 0.0:" + pointNoiseData);
    }
  }

  @Test
  void testValuesMultipliedByFactor() {
    defaultGenerator = defaultBuilder.build();
    Random random = new Random(System.currentTimeMillis());
    double amplitudeFactor = random.nextDouble() * 100;
    PointGenerator amplifiedLayer = defaultBuilder.withAmplitude(amplitudeFactor).build();

    Double[] values = new Double[expectedCount];
    Double[] actualAmplifiedValues = new Double[expectedCount];
    for (int i = 0; i < expectedCount; i++) {
      values[i] = defaultGenerator.getNext();
      actualAmplifiedValues[i] = amplifiedLayer.getNext();
    }

    for (int i = 0; i < values.length; i++) {
      values[i] = values[i] * amplitudeFactor;
    }
    Assertions.assertArrayEquals(values, actualAmplifiedValues);
  }

  @Test
  void testBuildNoisePointNotNull() {
    Assertions.assertNotNull(new PointGeneratorBuilder().build());
  }

  @Test
  void testCreateSamePoints() {
    defaultGenerator = defaultBuilder.build();
    PointGenerator sameLayer = defaultBuilder.build();
    for (int i = 0; i < expectedCount; i++) {
      Assertions.assertEquals(defaultGenerator.getNext(), sameLayer.getNext(), 0.0);
    }
  }

  @Test
  void testCreateDifferentPointsForDifferentSeed() {
    PointGenerator differentSeedGenerator = defaultBuilder.withRandomSeed(randomSeed + 1).build();
    for (int i = 0; i < expectedCount; i++) {
      Double val = defaultGenerator.getNext();
      Assertions.assertNotEquals(
          val,
          differentSeedGenerator.getNext(),
          "Values are equal for i: " + i + ", value: " + val);
    }
  }

  @ParameterizedTest
  @ValueSource(doubles = {-5, 0})
  void testCreateWithWrongStepSize(double noiseStepSize) {
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> defaultBuilder.withTimeStepSize(noiseStepSize));
  }

  @ParameterizedTest(name = "{index} - {2}")
  @MethodSource("testEqualsSource")
  @SuppressWarnings("unused")
  void testEquals(Object first, Object second, String title) {
    Assertions.assertEquals(first, second);
    Assertions.assertEquals(first.hashCode(), second.hashCode());
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testEqualsSource() throws StepSizeException {
    PointGeneratorBuilder other = resetBuilder(new PointGeneratorBuilder());
    defaultGenerator = resetBuilder(defaultBuilder).build();
    return Stream.of(
        Arguments.of(
            other.build(), defaultGenerator, "Different generators from different builders"),
        Arguments.of(other.build(), other.build(), "Different generators from same builder"),
        Arguments.of(defaultGenerator, defaultGenerator, "Same generator"));
  }

  @ParameterizedTest(name = "{index} - {1}")
  @MethodSource("testNotEqualsSource")
  @SuppressWarnings("unused")
  void testNotEquals(Object other, String title) {
    Assertions.assertNotEquals(defaultGenerator, other);
  }

  @SuppressWarnings("unused")
  private static Stream<Arguments> testNotEqualsSource() throws StepSizeException {
    return Stream.of(
        Arguments.of(null, "Comparison with null"),
        Arguments.of(new Random(), "Different object class"),
        Arguments.of(
            resetBuilder(defaultBuilder).withRandomSeed(randomSeed + 1).build(), "Different seed"),
        Arguments.of(
            resetBuilder(defaultBuilder).withAmplitude(amplitude + 1.0).build(),
            "Different amplitude"),
        Arguments.of(
            resetBuilder(defaultBuilder).withTimeStepSize(noiseStepSize + 1.0 / 8).build(),
            "Different noise step size"));
  }

  @Test
  void testToString() {
    ToStringVerifier.forClass(defaultGenerator.getClass())
        .withClassName(NameStyle.SIMPLE_NAME)
        .withPreset(Presets.INTELLI_J)
        .withIgnoredFields(
            "perlinData", "currentTimeDistance", "generated", "containers", "containersCount")
        .verify();
  }

  @Test
  void getNoiseStepSize() {
    Assertions.assertEquals(noiseStepSize, defaultGenerator.getTimeStepSize(), 1E-9);
  }
}
