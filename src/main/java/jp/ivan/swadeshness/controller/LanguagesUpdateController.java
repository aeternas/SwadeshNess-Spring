package jp.ivan.swadeshness.controller;

import jp.ivan.swadeshness.service.GitService;
import jp.ivan.swadeshness.service.GitServiceImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
public class LanguagesUpdateController {

    private GitService gitService;
    private ExecutorService executor;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/words/{word}", method = RequestMethod.PUT)
    public String index(@PathVariable String word) throws GitAPIException, IOException, ExecutionException, InterruptedException {
        gitService = getGitService();
        gitService.pushAll(word);
        return "Words list is updated with word" + word;
    }

    private ExecutorService getTaskExecutor() {
        if (executor != null) {
            return executor;
        }
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
