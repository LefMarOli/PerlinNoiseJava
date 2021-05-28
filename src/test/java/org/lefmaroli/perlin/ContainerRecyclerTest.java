package org.lefmaroli.perlin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lefmaroli.perlin.ContainerRecycler.ContainerCreator;

class ContainerRecyclerTest {

  private static class TestContainerCreator implements ContainerCreator<Object> {

    @Override
    public Object createNewContainer() {
      return new Object();
    }
  }

  private final TestContainerCreator testContainerCreator = new TestContainerCreator();

  @Test
  void testCreateNewContainers() {
    ContainerRecycler<Object> recycler = new ContainerRecycler<>(testContainerCreator);
    for (int i = 0; i < 5000; i++) {
      Assertions.assertNotNull(recycler.getNewOrNextAvailableContainer());
    }
  }

  @Test
  void testContainersDifferent() {
    ContainerRecycler<Object> recycler = new ContainerRecycler<>(testContainerCreator);
    Object container = recycler.getNewOrNextAvailableContainer();
    Object secondContainer = recycler.getNewOrNextAvailableContainer();
    Assertions.assertNotEquals(container, secondContainer);
  }

  @Test
  void testContainerRecycled() {
    ContainerRecycler<Object> recycler = new ContainerRecycler<>(testContainerCreator);
    Object container = recycler.getNewOrNextAvailableContainer();
    recycler.recycleContainer(container);
    Object secondContainer = recycler.getNewOrNextAvailableContainer();
    Assertions.assertEquals(container, secondContainer);
  }
}
