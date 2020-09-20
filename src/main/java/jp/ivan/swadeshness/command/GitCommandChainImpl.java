package jp.ivan.swadeshness.command;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class GitCommandChainImpl implements GitCommandChain {
    public List<Callable> commands;
    private ExecutorService executor;
    public void start() {
    }
}
