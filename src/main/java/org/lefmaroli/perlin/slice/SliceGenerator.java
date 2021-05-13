package org.lefmaroli.perlin.slice;

import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lefmaroli.perlin.PerlinNoise;
import org.lefmaroli.perlin.dimensional.MultiDimensionalRootNoiseGenerator;

public class SliceGenerator extends MultiDimensionalRootNoiseGenerator<double[][]>
    implements SliceNoiseGenerator {

  private static final Logger LOGGER = LogManager.getLogger(SliceGenerator.class);

  private static final List<String> parameterNames =
      List.of("Width step size", "Slice width", "Height step size", "Slice height");
  private final double widthStepSize;
  private final double heightStepSize;
  private final int sliceWidth;
  private final int sliceHeight;
  private final double res;
  private final PerlinNoise perlin;
  private final double[] perlinData;
  private int currentPosInNoiseInterpolation = 0;

  SliceGenerator(
      double noiseStepSize,
      double widthStepSize,
      double heightStepSize,
      int sliceWidth,
      int sliceHeight,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular) {
    super(noiseStepSize, maxAmplitude, randomSeed, isCircular);
    assertValidValues(parameterNames, widthStepSize, heightStepSize, sliceWidth, sliceHeight);
    this.widthStepSize = correctStepSizeForCircularity(widthStepSize, sliceWidth, "slice width");
    this.heightStepSize =
        correctStepSizeForCircularity(heightStepSize, sliceHeight, "slice height");
    this.sliceWidth = sliceWidth;
    this.sliceHeight = sliceHeight;
    double circularWidthResolution = 1.0 / (this.widthStepSize * this.sliceWidth);
    double circularHeightResolution = 1.0 / (this.heightStepSize * this.sliceHeight);
    this.res =
        Math.sqrt(
            circularWidthResolution * circularWidthResolution
                + circularHeightResolution * circularHeightResolution);
    if (isCircular) {
      perlin = new PerlinNoise(4, randomSeed);
      perlinData = new double[4];
    } else {
      perlin = new PerlinNoise(3, randomSeed);
      perlinData = new double[3];
    }
    LOGGER.debug("Create new {}", this);
  }

  public double getWidthStepSize() {
    return widthStepSize;
  }

  public double getHeightStepSize() {
    return heightStepSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SliceGenerator that = (SliceGenerator) o;
    return widthStepSize == that.widthStepSize
        && heightStepSize == that.heightStepSize
        && sliceWidth == that.sliceWidth
        && sliceHeight == that.sliceHeight;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), widthStepSize, heightStepSize, sliceWidth, sliceHeight);
  }

  @Override
  public String toString() {
    return "SliceGenerator{"
        + "noiseStepSize="
        + getNoiseStepSize()
        + ", widthStepSize="
        + widthStepSize
        + ", heightStepSize="
        + heightStepSize
        + ", sliceWidth="
        + sliceWidth
        + ", sliceHeight="
        + sliceHeight
        + ", maxAmplitude="
        + getMaxAmplitude()
        + ", randomSeed="
        + randomSeed
        + ", isCircular="
        + isCircular()
        + '}';
  }

  @Override
  public int getSliceWidth() {
    return sliceWidth;
  }

  @Override
  public int getSliceHeight() {
    return sliceHeight;
  }

  @Override
  protected double[][] getNewContainer() {
    return new double[sliceWidth][sliceHeight];
  }

  @Override
  protected double[][] generateNextSegment(double[][] container) {
    currentPosInNoiseInterpolation++;
    processNoiseDomain(currentPosInNoiseInterpolation, container);
    return container;
  }

  private void processNoiseDomain(int noiseIndex, double[][] slice) {
    double noiseDist = (double) (noiseIndex) * getNoiseStepSize();
    for (var widthIndex = 0; widthIndex < sliceWidth; widthIndex++) {
      processSliceWidthDomain(noiseDist, widthIndex, slice[widthIndex]);
    }
  }

  private void processSliceWidthDomain(double noiseDist, int widthIndex, double[] line) {
    double widthDist;
    if (isCircular()) {
      widthDist = widthIndex * widthStepSize * 2 * Math.PI;
    } else {
      widthDist = widthIndex * widthStepSize;
    }
    for (var heightIndex = 0; heightIndex < sliceHeight; heightIndex++) {
      line[heightIndex] = processSliceHeightDomain(noiseDist, widthDist, heightIndex);
    }
  }

  private double processSliceHeightDomain(double noiseDist, double widthDist, int heightIndex) {
    perlinData[0] = noiseDist;
    if (isCircular()) {
      double heightDist = heightIndex * heightStepSize * 2 * Math.PI;
      double cosHeight = Math.cos(heightDist);
      perlinData[1] = ((Math.sin(widthDist) * cosHeight) + 1.0) / 2.0 * res;
      perlinData[2] = ((Math.cos(widthDist) * cosHeight) + 1.0) / 2.0 * res;
      perlinData[3] = ((Math.sin(heightDist)) + 1.0) / 2.0 * res;
    } else {
      perlinData[1] = widthDist;
      perlinData[2] = heightIndex * heightStepSize;
    }
    return perlin.getFor(perlinData) * getMaxAmplitude();
  }
}
