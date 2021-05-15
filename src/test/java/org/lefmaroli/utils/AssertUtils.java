package org.lefmaroli.utils;

import java.util.Arrays;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;

public class AssertUtils {

  private static final ErrorMessageSupplier errorMessageSupplier = new ErrorMessageSupplier();

  public static void valuesContinuousInArray(double[] array, int[] signs) {
    int windowWidth = 2;
    for (int i = 1; i < array.length; i++) {
      if (Math.abs(array[i] - array[i - 1]) < 1E-4) {
        signs[i - 1] = 0;
      } else signs[i - 1] = array[i] - array[i - 1] > 0 ? 1 : -1;
    }

    for (int i = windowWidth; i < signs.length - windowWidth; i++) {
      int ref = signs[i - windowWidth];
      if (ref == 0) {
        int next = i - windowWidth + 1;
        while (signs[next] == 0 && next < i + windowWidth) {
          next++;
        }
        if (signs[next] == 0) {
          continue;
        } else {
          ref = signs[next];
        }
      }
      int transitionPoint = -1;
      for (int j = -windowWidth + 1; j < windowWidth; j++) {
        if (signs[i + j] != 0 && signs[i + j] != ref) {
          transitionPoint = i + j;
          break;
        }
      }
      if (transitionPoint != -1) {
        for (int j = i - windowWidth + 1; j < transitionPoint; j++) {
          if (signs[j] != 0) {
            Assertions.assertEquals(ref, signs[j], getErrorMessage(i, windowWidth, array));
          }
        }
        for (int j = transitionPoint + 1; j < i + windowWidth; j++) {
          if (signs[j] != 0) {
            Assertions.assertEquals(
                signs[transitionPoint], signs[j], getErrorMessage(i, windowWidth, array));
          }
        }
      }
    }
  }

  private static Supplier<String> getErrorMessage(int index, int windowWidth, double[] array) {
    return errorMessageSupplier.setIndex(index).setWindowWidth(windowWidth).setArray(array);
  }

  private static class ErrorMessageSupplier implements Supplier<String> {

    private double[] array;
    private int index;
    private int windowWidth;

    @Override
    public String get() {
      double[] subArray = Arrays.copyOfRange(array, index - windowWidth, index + windowWidth + 1);
      return "Array does not have continuous values:"
          + Arrays.toString(subArray)
          + " around i="
          + index;
    }

    public ErrorMessageSupplier setArray(double[] array) {
      this.array = array;
      return this;
    }

    public ErrorMessageSupplier setIndex(int index) {
      this.index = index;
      return this;
    }

    public ErrorMessageSupplier setWindowWidth(int windowWidth) {
      this.windowWidth = windowWidth;
      return this;
    }
  }
}
