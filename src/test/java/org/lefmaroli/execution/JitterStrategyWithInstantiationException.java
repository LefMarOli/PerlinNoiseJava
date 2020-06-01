package org.lefmaroli.execution;

public class JitterStrategyWithInstantiationException implements JitterStrategy{

    JitterStrategyWithInstantiationException(){
        throw new RuntimeException("For testing");
    }

    @Override
    public void jitter() {

    }
}
