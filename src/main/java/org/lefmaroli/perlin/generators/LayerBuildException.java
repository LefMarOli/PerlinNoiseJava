package org.lefmaroli.perlin.generators;

public class LayerBuildException extends Exception {

  private static final String MESSAGE_FORMAT = "Exception occurred while building layer number %s";

  LayerBuildException(int currentLayer, Throwable cause) {
    super(constructMessage(currentLayer), cause);
  }

  private static String constructMessage(int currentLayer) {
    return String.format(MESSAGE_FORMAT, currentLayer);
  }
}
