package jp.ivan.swadeshness.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.core.env.Environment;

import java.io.IOException;

public interface GitService {
//    void setEnv(Environment env);
    void pushAll(String message) throws IOException, GitAPIException;
}
