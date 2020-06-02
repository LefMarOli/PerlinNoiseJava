package org.lefmaroli.perlin.exceptions;

public class NoiseLayerException extends Exception {

  private static final String messageFormat =
      "Skipping generation of %s layers from a total of %s layers, no more interpolation possible. Current "
          + "layer: %s, please check provided distanceFactorGenerator";

  NoiseLayerException(int numberOfLayers, int currentLayer, Throwable cause) {
    super(constructMessage(numberOfLayers, currentLayer), cause);
  }

  private static String constructMessage(int numberOfLayers, int currentLayer) {
    int remainingLayers = numberOfLayers - currentLayer;
    return String.format(messageFormat, remainingLayers, numberOfLayers, currentLayer);
  }

  public static class Builder {
    private final int numberOfLayers;
    private final int currentLayers;
    private Throwable cause = null;

    public Builder(int numberOfLayers, int currentLayers) {
      this.numberOfLayers = numberOfLayers;
      this.currentLayers = currentLayers;
    }

    public Builder setCause(Throwable cause) {
      this.cause = cause;
      return this;
    }

    public NoiseLayerException build() {
      return new NoiseLayerException(numberOfLayers, currentLayers, cause);
    }
  }
}
