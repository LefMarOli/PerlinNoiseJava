package org.lefmaroli.execution;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.LogManager;

public class ContainerRecycler<T> {

  private final Queue<T> availableContainers = new ConcurrentLinkedQueue<>();
  private final ContainerCreator<T> containerCreator;

  public interface ContainerCreator<T>{
    T createNewContainer();
  }

  public ContainerRecycler(ContainerCreator<T> containerCreator) {
    this.containerCreator = containerCreator;
  }

  public void recycleContainer(T container){
    availableContainers.add(container);
  }

  public T getNewOrNextAvailableContainer(){
    var nextAvailable = availableContainers.poll();
    if(nextAvailable == null){
      return containerCreator.createNewContainer();
    }else{
      return nextAvailable;
    }
  }
}
