package jp.ivan.swadeshness.controller;

import jp.ivan.swadeshness.service.GitService;
import jp.ivan.swadeshness.service.GitServiceImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

@RestController
public class LanguagesUpdateController {

    private Logger logger = LoggerFactory.getLogger(LanguagesUpdateController.class);
    
    private GitService gitService;
    private ExecutorService executor;

    @Autowired
    private Environment env;

    @PutMapping(value = "/words/{word}")
    ResponseEntity<String> pushWord(@PathVariable String word) throws GitAPIException, IOException, ExecutionException, InterruptedException {
        gitService = getGitService();
        gitService.pushAll(word);
        return new ResponseEntity<>("Words list is updated with word " + word, HttpStatus.OK);
    }

    @PutMapping(value = "/words/")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<String> pushWords(@RequestBody String @NotNull [] words) throws GitAPIException, IOException, ExecutionException, InterruptedException {
        gitService = getGitService();
        for (String word : words) {
            gitService.pushAll(word);
        }
        return new ResponseEntity<>("Words list is updated with words: " + words, HttpStatus.CREATED);
    }

    private ExecutorService getTaskExecutor() {
        if (executor != null) {
            logger.debug("Using existing executor");
            return executor;
        }
        logger.debug("Instantiated new executor");
        executor = Executors.newSingleThreadExecutor();
        return executor;
    }

    private GitService getGitService() {
        if (gitService != null) {
            return gitService;
        }
        gitService = new GitServiceImpl();
        gitService.setExecutor(getTaskExecutor());
        gitService.setEnv(env);
        return gitService;
    }
}
