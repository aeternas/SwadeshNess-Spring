package jp.ivan.swadeshness.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public interface GitService {
    public void setEnv(Environment env);
    void setExecutor(ExecutorService executor);
    Git getRepo() throws IOException, GitAPIException;
    SshSessionFactory getSessionFactory();
    void pushAll(String message) throws IOException, GitAPIException, ExecutionException, InterruptedException;
}
