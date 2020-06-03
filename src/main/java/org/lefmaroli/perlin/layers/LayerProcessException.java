package org.lefmaroli.perlin.layers;

public class LayerProcessException extends RuntimeException{

  public LayerProcessException(Throwable cause) {
    super("Exception thrown while processing parallel layers", cause);
  }
}
