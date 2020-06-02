package org.lefmaroli.perlin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.lefmaroli.factorgenerator.DoubleGenerator;
import org.lefmaroli.factorgenerator.IntegerGenerator;
import org.lefmaroli.perlin.data.NoiseData;
import org.lefmaroli.perlin.exceptions.NoiseBuilderException;

public class AbstractNoiseGeneratorBuilderTest {

  @Test
  public void testBuilderPattern() {
    MockNoiseBuilder noisePointBuilder = new MockNoiseBuilder();
    assertNotNull(noisePointBuilder.withRandomSeed(0L));
    assertNotNull(noisePointBuilder.withNumberOfLayers(5));
    assertNotNull(noisePointBuilder.withAmplitudeGenerator(new DoubleGenerator(1, 1.0)));
    assertNotNull(
        noisePointBuilder.withNoiseInterpolationPointGenerator(new IntegerGenerator(1, 1.0)));
  }

  @Test(expected = NoiseBuilderException.class)
  public void testToFewInterpolationPoints() throws NoiseBuilderException {
    new MockNoiseBuilder()
        .withNoiseInterpolationPointGenerator(new IntegerGenerator(1, 0.5))
        .withNumberOfLayers(5)
        .build();
  }

  @Test(expected = NoiseBuilderException.class)
  public void testTooManyInterpolationPoints() throws NoiseBuilderException {
    new MockNoiseBuilder()
        .withNoiseInterpolationPointGenerator(new IntegerGenerator(1, 5000))
        .withNumberOfLayers(15)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNoLayers() {
    new MockNoiseBuilder().withNumberOfLayers(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateWithNegativeNumberOfLayers() {
    new MockNoiseBuilder().withNumberOfLayers(-8);
  }

  @Test
  public void testCreateSingleLayer() throws NoiseBuilderException {
    INoiseGenerator<MockNoiseData> built = new MockNoiseBuilder().withNumberOfLayers(1).build();
    assertTrue(built instanceof MockNoiseGeneratorLayer);
  }

  @Test(expected = NoiseBuilderException.class)
  public void testCreateSingleLayerWithNoInterpolationPoints() throws NoiseBuilderException {
    new MockNoiseBuilder()
        .withNumberOfLayers(1)
        .withNoiseInterpolationPointGenerator(new IntegerGenerator(0, 500))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWrongImplementationOfBuilderClass() {
    int dimensions = 5;
    new WrongSubClassImplementationMock(dimensions)
        .setInterpolationPointCountGeneratorForDimension(
            dimensions + 1, new IntegerGenerator(1, 0.5));
  }

  private static class MockNoiseData implements NoiseData<Double, MockNoiseData> {

    @Override
    public void add(MockNoiseData other) {}

    @Override
    public void normalizeBy(double maxValue) {}

    @Override
    public Double getAsRawData() {
      return 0.0;
    }
  }

  private static class MockNoiseGenerator implements INoiseGenerator<MockNoiseData> {

    @Override
    public boolean equals(Object other) {
      return false;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public int getDimensions() {
      return 0;
    }

    @Override
    public MockNoiseData getNext(int count) {
      return new MockNoiseData();
    }

    @Override
    public double getMaxAmplitude() {
      return 0.0;
    }
  }

  private static class MockNoiseGeneratorLayer extends MockNoiseGenerator {}

  private static class MockNoiseBuilder
      extends NoiseBuilder<MockNoiseData, MockNoiseGenerator, MockNoiseBuilder> {

    MockNoiseBuilder() {
      super(5);
    }

    @Override
    protected MockNoiseBuilder self() {
      return this;
    }

    @Override
    protected MockNoiseGenerator buildSingleNoiseLayer(
        List<Integer> interpolationPoints, double layerAmplitude, long randomSeed) {
      return new MockNoiseGeneratorLayer();
    }

    @Override
    protected MockNoiseGenerator buildMultipleNoiseLayer(List<MockNoiseGenerator> layers) {
      return new MockNoiseGenerator();
    }
  }

  private static class WrongSubClassImplementationMock
      extends NoiseBuilder<MockNoiseData, MockNoiseGenerator, WrongSubClassImplementationMock> {

    public WrongSubClassImplementationMock(int dimensions) {
      super(dimensions);
    }

    @Override
    protected WrongSubClassImplementationMock self() {
      return this;
    }

    @Override
    protected MockNoiseGenerator buildSingleNoiseLayer(
        List<Integer> interpolationPoints, double layerAmplitude, long randomSeed) {
      return null;
    }

    @Override
    protected MockNoiseGenerator buildMultipleNoiseLayer(List<MockNoiseGenerator> layers) {
      return null;
    }
  }
}
