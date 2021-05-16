package org.lefmaroli.execution;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExecutorPool implements AutoCloseable, ExecutorService {

  private static final int DEFAULT_THREADS = 5;
  private final Logger logger;

  private final ExecutorService pool;

  ExecutorPool(String className) {
    this(className, Executors.newFixedThreadPool(DEFAULT_THREADS));
  }

  ExecutorPool(String className, ExecutorService service) {
    this.logger = LogManager.getLogger(className);
    this.pool = service;
  }

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

  //Delegate functions
  @Override
  public void shutdown() {
    pool.shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return pool.shutdownNow();
  }

  @Override
  public boolean isShutdown() {
    return pool.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return pool.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return pool.awaitTermination(timeout, unit);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return pool.submit(task);
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return pool.submit(task, result);
  }

  @Override
  public Future<?> submit(Runnable task) {
    return pool.submit(task);
  }

  @Override
  public <T> List<Future<T>> invokeAll(
      Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return pool.invokeAll(tasks);
  }

  @Override
  public <T> List<Future<T>> invokeAll(
      Collection<? extends Callable<T>> tasks, long timeout,
      TimeUnit unit) throws InterruptedException {
    return pool.invokeAll(tasks, timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException {
    return pool.invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout,
      TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return pool.invokeAny(tasks, timeout, unit);
  }

  @Override
  public void execute(Runnable command) {
    pool.execute(command);
  }

}
