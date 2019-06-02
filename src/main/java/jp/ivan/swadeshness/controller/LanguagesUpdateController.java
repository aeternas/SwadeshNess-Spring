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


@RestController
public class LanguagesUpdateController {

    public GitService gitService;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/words/{word}", method = RequestMethod.PUT)
    public String index(@PathVariable String word) throws GitAPIException, IOException {
        gitService = new GitServiceImpl();
//        gitService.setEnv(env);
        gitService.pushAll(word);
        return "Words list is updated with word";
    }
}
