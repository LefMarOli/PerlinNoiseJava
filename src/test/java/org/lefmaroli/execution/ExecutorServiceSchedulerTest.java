package org.lefmaroli.execution;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;

public class ExecutorServiceSchedulerTest {

    @Test
    public void testExecutorServiceSchedulerWithTaskException() throws InterruptedException {
        Callable<Double> callableThatThrows = () -> {
            throw new Exception("Testing exception handling");
        };
        ExecutorServiceScheduler scheduler = new ExecutorServiceScheduler(10);
        CompletableFuture<Double> future = scheduler.schedule(callableThatThrows);
        while(!future.isCompletedExceptionally()){
            Thread.sleep(2);
        }
        assertTrue(future.isCompletedExceptionally());
    }

}