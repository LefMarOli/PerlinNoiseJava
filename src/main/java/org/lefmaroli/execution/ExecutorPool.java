package org.lefmaroli.execution;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExecutorPool implements AutoCloseable{

  private static final int DEFAULT_THREADS = 5;
  private final Logger logger;
  private final ExecutorService pool;

  ExecutorPool(String className){
    this(className, Executors.newFixedThreadPool(DEFAULT_THREADS));
  }

  ExecutorPool(String className, ExecutorService service){
    this.logger = LogManager.getLogger(className);
    this.pool = service;
  }

//  public <T> Future<T> submit(Runnable r, T type){
//
//  }

  @Override
  public void close() {
    pool.shutdownNow();
    try {
      boolean areTasksDone = pool.awaitTermination(1, TimeUnit.SECONDS);
      if (!areTasksDone) {
        logger.error("Tasks were not completed within the delay");
      } else logger.debug("ExecutorService shutdown complete");
    } catch (InterruptedException e) {
      logger.error("Awaiting task termination was interrupted");
      Thread.currentThread().interrupt();
    }
  }
}
