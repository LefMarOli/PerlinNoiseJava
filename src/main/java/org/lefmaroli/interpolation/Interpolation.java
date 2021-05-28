package org.lefmaroli.interpolation;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.lefmaroli.perlin.PerlinNoise;

public class Interpolation {

  private static final Map<Integer, Map.Entry<String, String>> FORMAT_MAP = new HashMap<>(3);

  static {
    FORMAT_MAP.put(2, Map.entry("Plane", "x and y"));
    FORMAT_MAP.put(3, Map.entry("Cube", "x, y and z"));
    FORMAT_MAP.put(4, Map.entry("Hypercube", "x, y, z and t"));
  }

  private Interpolation() {}

  public static double linear(CornerMatrix cornerMatrix, double[] distances) {
    checkDistances(distances, cornerMatrix.getDimension());
    return linearUnchecked(cornerMatrix, distances);
  }

  private static double linearUnchecked(CornerMatrix cornerMatrix, double[] distances) {
    if (cornerMatrix.getDimension() == 1) {
      return linearUnchecked(
          cornerMatrix.get(0), cornerMatrix.get(1), distances[distances.length - 1]);
    } else {
      double firstInterpolation = linearUnchecked(cornerMatrix.getSubMatrix(0), distances);
      double secondInterpolation = linearUnchecked(cornerMatrix.getSubMatrix(1), distances);
      return linearUnchecked(
          firstInterpolation,
          secondInterpolation,
          distances[distances.length - cornerMatrix.getDimension()]);
    }
  }

  public static double linearWithFade(CornerMatrix cornerMatrix, double[] distances) {
    checkDistances(distances, cornerMatrix.getDimension());
    return linearWithFadeUnchecked(cornerMatrix, distances);
  }

  private static double linearWithFadeUnchecked(CornerMatrix cornerMatrix, double[] distances) {
    if (Thread.currentThread().isInterrupted()) {
      LogManager.getLogger(PerlinNoise.class)
          .debug("Interrupting processing [linearWithFadeUnchecked]");
      return 0.0;
    }
    if (cornerMatrix.getDimension() == 1) {
      return linearWithFadeUnchecked(
          cornerMatrix.get(0), cornerMatrix.get(1), distances[distances.length - 1]);
    } else {
      double firstInterpolation = linearWithFadeUnchecked(cornerMatrix.getSubMatrix(0), distances);
      double secondInterpolation = linearWithFadeUnchecked(cornerMatrix.getSubMatrix(1), distances);
      return linearWithFadeUnchecked(
          firstInterpolation,
          secondInterpolation,
          distances[distances.length - cornerMatrix.getDimension()]);
    }
  }

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

  private static String getDimensionsOrderForDimension(int dimension) {
    return FORMAT_MAP.get(dimension).getValue();
  }

  private static void checkDistances(double[] distances, int numberOfDimensions) {
    if (distances.length != numberOfDimensions) {
      throw new DistancesArrayLengthException(
          numberOfDimensions, getDimensionsOrderForDimension(numberOfDimensions));
    }
    for (var i = 0; i < numberOfDimensions; i++) {
      if (distances[i] < 0.0 || distances[i] > 1.0) {
        throw new DistanceNotBoundedException(i);
      }
    }
  }

  private static double linearUnchecked(double y1, double y2, double distance) {
    return y1 + distance * (y2 - y1);
  }

  private static double linearWithFadeUnchecked(double y1, double y2, double distance) {
    return linearUnchecked(y1, y2, fadeUnchecked(distance));
  }

  private static double fadeUnchecked(double value) {
    double valueCubed = value * value * value;
    // 6t^5 - 15t^4 + 10t^3
    return 6 * valueCubed * value * value - 15 * valueCubed * value + 10 * valueCubed;
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
      if (index < 0 || index >= Dimension.values().length) {
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
