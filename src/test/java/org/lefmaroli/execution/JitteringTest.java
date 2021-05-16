package org.lefmaroli.execution;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.lefmaroli.configuration.JitterTrait;

public abstract class JitteringTest {

  @BeforeAll
  void beforeAll(){
    JitterTrait.setJitterStrategy(new TestJitterStrategy());
  }

  @AfterAll
  void afterAll(){
    JitterTrait.resetJitterStrategy();
  }

}
