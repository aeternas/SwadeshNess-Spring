package jp.ivan.swadeshness.controller;

import jp.ivan.swadeshness.service.GitService;
import jp.ivan.swadeshness.service.GitServiceImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class LanguagesUpdateController {

    private Logger logger = LoggerFactory.getLogger(LanguagesUpdateController.class);
    
    private GitService gitService;
    private ExecutorService executor;

    @Autowired
    private Environment env;

    @PutMapping(value = "/words/{word}")
    ResponseEntity<String> index(@PathVariable String word) throws GitAPIException, IOException, ExecutionException, InterruptedException {
        gitService = getGitService();
        gitService.pushAll(word);
        return new ResponseEntity<>("Words list is updated with word " + word, HttpStatus.OK);
    }

    private ExecutorService getTaskExecutor() {
        if (executor != null) {
            logger.debug("Instantiated new executor");
            return executor;
        }
        logger.debug("Using existing executor");
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
