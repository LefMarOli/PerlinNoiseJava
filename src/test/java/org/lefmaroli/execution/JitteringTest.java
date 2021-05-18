package org.lefmaroli.execution;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.lefmaroli.configuration.JitterTrait;

public abstract class JitteringTest {

  @BeforeAll
  static void beforeAll(){
    JitterTrait.setJitterStrategy(new TestJitterStrategy());
  }

  @AfterAll
  static void afterAll(){
    JitterTrait.resetJitterStrategy();
  }

}
