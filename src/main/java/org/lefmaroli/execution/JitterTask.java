package org.lefmaroli.execution;

import java.util.concurrent.Callable;

public abstract class JitterTask<ResultType> implements Callable<ResultType> {

    private final JitterStrategy jitterStrategy;

    public JitterTask(JitterStrategy jitterStrategy){
        this.jitterStrategy = jitterStrategy;
    }

    protected abstract ResultType process();

    @Override
    public ResultType call() {
        jitterStrategy.jitter();
        ResultType result = process();
        jitterStrategy.jitter();
        return result;
    }
}
