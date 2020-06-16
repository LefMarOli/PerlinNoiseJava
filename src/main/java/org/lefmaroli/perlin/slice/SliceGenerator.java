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
      List.of(
          "Width interpolation points",
          "Slice width",
          "Height interpolation points",
          "Slice height");
  private final int widthInterpolationPoints;
  private final int heightInterpolationPoints;
  private final int sliceWidth;
  private final int sliceHeight;
  private final double[] line;
  private final int noiseSegmentLength;
  private int currentPosInNoiseInterpolation = 0;
  private final double circularWidthResolution;
  private final double circularHeightResolution;
  private final PerlinNoise perlin;
  private final double[] perlinData;

  SliceGenerator(
      int noiseInterpolationPoints,
      int widthInterpolationPoint,
      int heightInterpolationPoint,
      int sliceWidth,
      int sliceHeight,
      double maxAmplitude,
      long randomSeed,
      boolean isCircular) {
    super(noiseInterpolationPoints, maxAmplitude, randomSeed, isCircular);
    assertValidValues(
        parameterNames, widthInterpolationPoint, heightInterpolationPoint, sliceWidth, sliceHeight);
    this.widthInterpolationPoints =
        correctInterpolationPointsForCircularity(
            widthInterpolationPoint, sliceWidth, "slice width");
    this.heightInterpolationPoints =
        correctInterpolationPointsForCircularity(
            heightInterpolationPoint, sliceHeight, "slice height");
    this.sliceWidth = sliceWidth;
    this.sliceHeight = sliceHeight;
    this.noiseSegmentLength = computeNoiseSegmentLength(sliceWidth, sliceHeight);
    line = new double[sliceHeight];
    this.circularWidthResolution = this.widthInterpolationPoints / (double) this.sliceWidth;
    this.circularHeightResolution = this.heightInterpolationPoints / (double) this.sliceHeight;
    if(isCircular){
      perlin = new PerlinNoise(5);
      perlinData = new double[5];
    }else{
      perlin = new PerlinNoise(3);
      perlinData = new double[3];
    }
    LOGGER.debug("Create new {}", this);
  }

  public int getWidthInterpolationPoints() {
    return widthInterpolationPoints;
  }

  public int getHeightInterpolationPoints() {
    return heightInterpolationPoints;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SliceGenerator that = (SliceGenerator) o;
    return widthInterpolationPoints == that.widthInterpolationPoints
        && heightInterpolationPoints == that.heightInterpolationPoints
        && sliceWidth == that.sliceWidth
        && sliceHeight == that.sliceHeight;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        widthInterpolationPoints,
        heightInterpolationPoints,
        sliceWidth,
        sliceHeight);
  }

  @Override
  public String toString() {
    return "SliceGenerator{"
        + "noiseInterpolationPoints="
        + getNoiseInterpolationPoints()
        + ", widthInterpolationPoints="
        + widthInterpolationPoints
        + ", heightInterpolationPoints="
        + heightInterpolationPoints
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
  public int getNoiseSegmentLength() {
    return noiseSegmentLength;
  }

  @Override
  protected double[][][] generateNextSegment() {
    double[][][] results = new double[getNoiseSegmentLength()][sliceWidth][sliceHeight];
    for (int i = 0; i < noiseSegmentLength; i++) {
      currentPosInNoiseInterpolation++;
      System.arraycopy(
          processNoiseDomain(currentPosInNoiseInterpolation), 0, results[i], 0, sliceWidth);
    }
    return results;
  }

  @Override
  protected double[][][] getArrayOfSubType(int count) {
    return new double[count][sliceWidth][sliceHeight];
  }

  private int computeNoiseSegmentLength(int sliceWidth, int sliceHeight) {
    int computedNoiseSegmentLength =
        Math.min(MB_10_IN_DOUBLES_SIZE / (sliceWidth * sliceHeight), getNoiseInterpolationPoints());
    if (computedNoiseSegmentLength < 1) {
      computedNoiseSegmentLength = 1;
      int estimatedSliceSize = sliceWidth * sliceHeight / MB_10_IN_DOUBLES_SIZE * 10;
      LOGGER.warn(
          "Creating slice generator of more than 10MB in size (Estimated at {}MB",
          estimatedSliceSize);
    }
    return computedNoiseSegmentLength;
  }

  private double[][] processNoiseDomain(int noiseIndex) {
    double[][] slice = new double[sliceWidth][sliceHeight];
    double noiseDist = (double) (noiseIndex) * getStepSize();
    for (int widthIndex = 0; widthIndex < sliceWidth; widthIndex++) {
      System.arraycopy(
          processSliceWidthDomain(noiseDist, widthIndex), 0, slice[widthIndex], 0, sliceHeight);
    }
    return slice;
  }

  private double[] processSliceWidthDomain(double noiseDist, int widthIndex) {
    double widthDist;
    if (isCircular()) {
      widthDist = widthIndex / (double) sliceWidth * 2 * Math.PI;
    } else {
      widthDist = (double) (widthIndex) / (widthInterpolationPoints);
    }
    for (int heightIndex = 0; heightIndex < sliceHeight; heightIndex++) {
      line[heightIndex] = processSliceHeightDomain(noiseDist, widthDist, heightIndex);
    }
    return line;
  }

  private double processSliceHeightDomain(double noiseDist, double widthDist, int heightIndex) {
    perlinData[0] = noiseDist;
    if (isCircular()) {
      perlinData[1] = (Math.cos(widthDist) * circularWidthResolution) + circularWidthResolution;
      perlinData[2] = (Math.sin(widthDist) * circularWidthResolution) + circularWidthResolution;
      double heightDist = heightIndex / (double) sliceHeight * 2 * Math.PI;
      perlinData[3] = (Math.cos(heightDist) * circularHeightResolution) + circularHeightResolution;
      perlinData[4] = (Math.sin(heightDist) * circularHeightResolution) + circularHeightResolution;
    } else {
      perlinData[1] = widthDist;
      perlinData[2] = (double) (heightIndex) / (heightInterpolationPoints);
    }
    return perlin.getFor(perlinData) * getMaxAmplitude();
  }
}
