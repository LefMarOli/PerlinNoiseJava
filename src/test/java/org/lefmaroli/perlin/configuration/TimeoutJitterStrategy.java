package org.lefmaroli.perlin.configuration;


import org.apache.logging.log4j.LogManager;

public class TimeoutJitterStrategy implements JitterStrategy{

  @Override
  public void jitter() {
    try {
      Thread.sleep(50000);
    } catch (InterruptedException e) {
      LogManager.getLogger(this.getClass()).error("Jitter strategy interrupted while waiting");
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public long getTimeout() {
    return 1;
  }
}
