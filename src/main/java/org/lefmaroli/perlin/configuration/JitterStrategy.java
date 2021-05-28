package org.lefmaroli.perlin.configuration;

public interface JitterStrategy {

  void jitter();

  long getTimeout();
}
