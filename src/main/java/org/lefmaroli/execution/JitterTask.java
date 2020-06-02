package org.lefmaroli.execution;

import org.lefmaroli.configuration.ConfigurationLoader;

import java.util.concurrent.Callable;

public abstract class JitterTask<ResultType> implements Callable<ResultType> {

    private static final JitterStrategy JITTER_STRATEGY = ConfigurationLoader.getJitterStrategy();

    @Override
    public ResultType call() {
        JITTER_STRATEGY.jitter();
        ResultType result = process();
        JITTER_STRATEGY.jitter();
        return result;
    }

    protected abstract ResultType process();
}
