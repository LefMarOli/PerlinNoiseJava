package org.lefmaroli.interpolation;

public class Interpolation {

  private Interpolation(){}

  private static final String SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT =
      "%s should be bounded between [0.0, 1.0]";

  public static double linear(double y1, double y2, double mu) {
    if (mu < 0.0 || mu > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, ""));
    }
    return linearUnchecked(y1, y2, mu);
  }

  public static double linearWithFade(double y1, double y2, double mu) {
    return linearUnchecked(y1, y2, fade(mu));
  }

  public static double fade(double value) {
    if (value < 0.0 || value > 1.0) {
      throw new IllegalArgumentException(
          String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "Value to fade"));
    }
    return fadeUnchecked(value);
  }

  public static double linear2D(
      double x1y1, double x1y2, double x2y1, double x2y2, double muX, double muY) {
    if (muX < 0.0 || muX > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuX"));
    }
    if (muY < 0.0 || muY > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuY"));
    }
    return linear2DUnchecked(x1y1, x1y2, x2y1, x2y2, muX, muY);
  }

  public static double linear2DWithFade(
      double x1y1, double x1y2, double x2y1, double x2y2, double muX, double muY) {
    if (muX < 0.0 || muX > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuX"));
    }
    if (muY < 0.0 || muY > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuY"));
    }
    return linear2DWithFadeUnchecked(x1y1, x1y2, x2y1, x2y2, muX, muY);
  }

  public static double linear3D(
      double x1y1z1,
      double x1y1z2,
      double x1y2z1,
      double x1y2z2,
      double x2y1z1,
      double x2y1z2,
      double x2y2z1,
      double x2y2z2,
      double muX,
      double muY,
      double muZ) {
    if (muX < 0.0 || muX > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuX"));
    }
    if (muY < 0.0 || muY > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuY"));
    }
    if (muZ < 0.0 || muZ > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuZ"));
    }
    return linear3DUnchecked(
        x1y1z1, x1y1z2, x1y2z1, x1y2z2, x2y1z1, x2y1z2, x2y2z1, x2y2z2, muX, muY, muZ);
  }

  public static double linear3DWithFade(
      double x1y1z1,
      double x1y1z2,
      double x1y2z1,
      double x1y2z2,
      double x2y1z1,
      double x2y1z2,
      double x2y2z1,
      double x2y2z2,
      double muX,
      double muY,
      double muZ) {
    if (muX < 0.0 || muX > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuX"));
    }
    if (muY < 0.0 || muY > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuY"));
    }
    if (muZ < 0.0 || muZ > 1.0) {
      throw new IllegalArgumentException(String.format(SHOULD_BE_BOUNDED_BETWEEN_0_1_FORMAT, "MuZ"));
    }
    return linear3DWithFadeUnchecked(
        x1y1z1, x1y1z2, x1y2z1, x1y2z2, x2y1z1, x2y1z2, x2y2z1, x2y2z2, muX, muY, muZ);
  }

  private static double linearUnchecked(double y1, double y2, double mu) {
    return y1 * (1 - mu) + y2 * mu;
  }

  private static double linearWithFadeUnchecked(double y1, double y2, double mu) {
    return linearUnchecked(y1, y2, fadeUnchecked(mu));
  }

  private static double fadeUnchecked(double value) {
    double valueCubed = value * value * value;
    // 6t^5 - 15t^4 + 10t^3
    return 6 * valueCubed * value * value - 15 * valueCubed * value + 10 * valueCubed;
  }

  private static double linear2DUnchecked(
      double x1y1, double x1y2, double x2y1, double x2y2, double muX, double muY) {
    double topInterpolation = Interpolation.linearUnchecked(x1y1, x2y1, muX);
    double bottomInterpolation = Interpolation.linearUnchecked(x1y2, x2y2, muX);
    return Interpolation.linearUnchecked(topInterpolation, bottomInterpolation, muY);
  }

  private static double linear2DWithFadeUnchecked(
      double x1y1, double x1y2, double x2y1, double x2y2, double muX, double muY) {
    double topInterpolation = Interpolation.linearWithFadeUnchecked(x1y1, x2y1, muX);
    double bottomInterpolation = Interpolation.linearWithFadeUnchecked(x1y2, x2y2, muX);
    return Interpolation.linearWithFadeUnchecked(topInterpolation, bottomInterpolation, muY);
  }

  private static double linear3DUnchecked(
      double x1y1z1,
      double x1y1z2,
      double x1y2z1,
      double x1y2z2,
      double x2y1z1,
      double x2y1z2,
      double x2y2z1,
      double x2y2z2,
      double muX,
      double muY,
      double muZ) {
    double frontInterpolation = linear2DUnchecked(x1y1z1, x1y2z1, x2y1z1, x2y2z1, muX, muY);
    double backInterpolation = linear2DUnchecked(x1y1z2, x1y2z2, x2y1z2, x2y2z2, muX, muY);
    return linearUnchecked(frontInterpolation, backInterpolation, muZ);
  }

  private static double linear3DWithFadeUnchecked(
      double x1y1z1,
      double x1y1z2,
      double x1y2z1,
      double x1y2z2,
      double x2y1z1,
      double x2y1z2,
      double x2y2z1,
      double x2y2z2,
      double muX,
      double muY,
      double muZ) {
    double frontInterpolation = linear2DWithFadeUnchecked(x1y1z1, x1y2z1, x2y1z1, x2y2z1, muX, muY);
    double backInterpolation = linear2DWithFadeUnchecked(x1y1z2, x1y2z2, x2y1z2, x2y2z2, muX, muY);
    return linearWithFadeUnchecked(frontInterpolation, backInterpolation, muZ);
  }
}
