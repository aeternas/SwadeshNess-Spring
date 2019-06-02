package jp.ivan.swadeshness.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

public interface GitService {
    void pushAll(String message) throws IOException, GitAPIException;
}
