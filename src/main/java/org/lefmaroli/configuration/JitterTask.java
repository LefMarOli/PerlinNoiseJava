package org.lefmaroli.configuration;

import java.util.concurrent.Callable;

public abstract class JitterTask<R> implements Callable<R> {

  private static final JitterStrategy JITTER_STRATEGY = ConfigurationLoader.getJitterStrategy();

  @Override
  public R call() {
    JITTER_STRATEGY.jitter();
    var result = process();
    JITTER_STRATEGY.jitter();
    return result;
  }

  protected abstract R process();
}
