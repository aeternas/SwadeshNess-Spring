package jp.ivan.swadeshness.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public interface GitService {
    void setEnv(Environment env);
    void setExecutor(ExecutorService executor);
    void pushAll(String message) throws IOException, GitAPIException, ExecutionException, InterruptedException;
}
