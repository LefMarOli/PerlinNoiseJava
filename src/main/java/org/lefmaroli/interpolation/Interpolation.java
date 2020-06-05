package org.lefmaroli.interpolation;

import java.util.HashMap;
import java.util.Map;

public class Interpolation {

  private static final Map<Integer, Map.Entry<String, String>> FORMAT_MAP = new HashMap<>(3);

  static {
    FORMAT_MAP.put(2, Map.entry("Plane", "x and y"));
    FORMAT_MAP.put(3, Map.entry("Cube", "x, y and z"));
    FORMAT_MAP.put(4, Map.entry("Hypercube", "x, y, z and t"));
  }

  private Interpolation() {}

  public static double linear(double y1, double y2, double mu) {
    if (mu < 0.0 || mu > 1.0) {
      throw new DistanceNotBoundedException(Dimension.X.index);
    }
    return linearUnchecked(y1, y2, mu);
  }

  public static double linearWithFade(double y1, double y2, double mu) {
    return linearUnchecked(y1, y2, fade(mu));
  }

  public static double fade(double value) {
    if (value < 0.0 || value > 1.0) {
      throw new ValueNotBoundedException("Value to fade");
    }
    return fadeUnchecked(value);
  }

  public static double linear2D(double[][] planeCorners, double[] distances) {
    checkCorners(planeCorners, 2);
    checkDistances(distances, 2);
    return linear2DUnchecked(planeCorners, distances);
  }

  private static <T> void checkCorners(T[] corners, int numberOfDimensions) {
    if (!isArrayDimension2(corners)) {
      throw new CornersArrayLengthException(
          getCornerNameForDimension(numberOfDimensions),
          numberOfDimensions,
          getDimensionsOrderForDimension(numberOfDimensions));
    }
  }

  private static String getCornerNameForDimension(int dimension) {
    return FORMAT_MAP.get(dimension).getKey();
  }

  private static String getDimensionsOrderForDimension(int dimension) {
    return FORMAT_MAP.get(dimension).getValue();
  }

  private static void checkDistances(double[] distances, int numberOfDimensions) {
    if (distances.length != numberOfDimensions) {
      throw new DistancesArrayLengthException(
          numberOfDimensions, getDimensionsOrderForDimension(numberOfDimensions));
    }
    for (int i = 0; i < 2; i++) {
      if (distances[i] < 0.0 || distances[i] > 1.0) {
        throw new DistanceNotBoundedException(i);
      }
    }
  }

  private static <T> boolean isArrayDimension2(T[] array) {
      try{
        return array.length == 2 && isArrayDimension2((T[]) array[0]);
      }catch (ClassCastException e){
        return true;
      }
  }

  public static double linear2DWithFade(double[][] planeCorners, double[] distances) {
    checkCorners(planeCorners, 2);
    checkDistances(distances, 2);
    return linear2DWithFadeUnchecked(planeCorners, distances);
  }

  public static double linear3D(double[][][] cubeCorners, double[] distances) {
    checkCorners(cubeCorners, 3);
    checkDistances(distances, 3);
    return linear3DUnchecked(cubeCorners, distances);
  }

  public static double linear3DWithFade(double[][][] cubeCorners, double[] distances) {
    checkCorners(cubeCorners, 3);
    checkDistances(distances, 3);
    return linear3DWithFadeUnchecked(cubeCorners, distances);
  }

  private static double linearUnchecked(double[] lineCorners, double distance) {
    return linearUnchecked(lineCorners[0], lineCorners[1], distance);
  }

  private static double linearUnchecked(double y1, double y2, double distance) {
    return y1 + distance * (y2 - y1);
  }

  private static double linearWithFadeUnchecked(double[] lineCorners, double distance) {
    return linearUnchecked(lineCorners, fadeUnchecked(distance));
  }

  private static double linearWithFadeUnchecked(double y1, double y2, double distance) {
    return linearUnchecked(y1, y2, fadeUnchecked(distance));
  }

  private static double fadeUnchecked(double value) {
    double valueCubed = value * value * value;
    // 6t^5 - 15t^4 + 10t^3
    return 6 * valueCubed * value * value - 15 * valueCubed * value + 10 * valueCubed;
  }

  private static double linear2DUnchecked(double[][] planeCorners, double[] distances) {
    int lastDimensionIndex = distances.length - 1;
    int previousDimensionIndex = lastDimensionIndex - 1;
    double firstInterpolation = linearUnchecked(planeCorners[0], distances[lastDimensionIndex]);
    double secondInterpolation = linearUnchecked(planeCorners[1], distances[lastDimensionIndex]);
    return linearUnchecked(
        firstInterpolation, secondInterpolation, distances[previousDimensionIndex]);
  }

  private static double linear2DWithFadeUnchecked(double[][] planeCorners, double[] distances) {
    int lastDimensionIndex = distances.length - 1;
    int previousDimensionIndex = lastDimensionIndex - 1;
    double firstInterpolation =
        linearWithFadeUnchecked(planeCorners[0], distances[lastDimensionIndex]);
    double secondInterpolation =
        linearWithFadeUnchecked(planeCorners[1], distances[lastDimensionIndex]);
    return linearWithFadeUnchecked(
        firstInterpolation, secondInterpolation, distances[previousDimensionIndex]);
  }

  private static double linear3DUnchecked(double[][][] cubeCorners, double[] distances) {
    int secondToLastDimensionIndex = distances.length - 3;
    double firstInterpolation = linear2DUnchecked(cubeCorners[0], distances);
    double secondInterpolation = linear2DUnchecked(cubeCorners[1], distances);
    return linearUnchecked(
        firstInterpolation, secondInterpolation, distances[secondToLastDimensionIndex]);
  }

  private static double linear3DWithFadeUnchecked(double[][][] cubeCorners, double[] distances) {
    int secondToLastDimensionIndex = distances.length - 3;
    double firstInterpolation = linear2DWithFadeUnchecked(cubeCorners[0], distances);
    double secondInterpolation = linear2DWithFadeUnchecked(cubeCorners[1], distances);
    return linearWithFadeUnchecked(
        firstInterpolation, secondInterpolation, distances[secondToLastDimensionIndex]);
  }

  public static double linear4D(double[][][][] hypercubeCorners, double[] distances) {
    checkCorners(hypercubeCorners, 4);
    checkDistances(distances, 4);
    return linear4DUnchecked(hypercubeCorners, distances);
  }

  public static double linear4DWithFade(double[][][][] hypercubeCorners, double[] distances) {
    checkCorners(hypercubeCorners, 4);
    checkDistances(distances, 4);
    return linear4DWithFadeUnchecked(hypercubeCorners, distances);
  }

  private static double linear4DWithFadeUnchecked(
      double[][][][] hypercubeCorners, double[] distances) {
    int thirdToLastIndex = distances.length - 4;
    double firstInterpolation = linear3DWithFadeUnchecked(hypercubeCorners[0], distances);
    double secondInterpolation = linear3DWithFadeUnchecked(hypercubeCorners[1], distances);
    return linearWithFadeUnchecked(
        firstInterpolation, secondInterpolation, distances[thirdToLastIndex]);
  }

  private static double linear4DUnchecked(double[][][][] hypercubeCorners, double[] distances) {
    int thirdToLastIndex = distances.length - 4;
    double firstInterpolation = linear3DUnchecked(hypercubeCorners[0], distances);
    double secondInterpolation = linear3DUnchecked(hypercubeCorners[1], distances);
    return linearUnchecked(firstInterpolation, secondInterpolation, distances[thirdToLastIndex]);
  }

  enum Dimension {
    X(0, "X"),
    Y(1, "Y"),
    Z(2, "Z"),
    T(3, "T");

    private final int index;
    private final String name;

    Dimension(int index, String name) {
      this.index = index;
      this.name = name;
    }

    public static Dimension getFromIndex(int index) {
      if (index > 3 || index < 0) {
        throw new IllegalArgumentException("Supported dimensions from 0(X) to 3(T)");
      }
      for (Dimension value : Dimension.values()) {
        if (value.index == index) {
          return value;
        }
      }
      return null;
    }

    public String getName() {
      return name;
    }
  }
}
