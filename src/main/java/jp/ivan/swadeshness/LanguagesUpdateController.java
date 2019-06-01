package jp.ivan.swadeshness;

import com.fasterxml.jackson.databind.BeanProperty;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

@RestController
public class LanguagesUpdateController {

    @RequestMapping("/")
    public String index() throws GitAPIException, IOException {
        Git git = Git.cloneRepository()
                .setURI( "https://github.com/aeternas/SwadeshNess-words-list.git" )
                .call();
        FileWriter fileWriter = new FileWriter("SwadeshNess-words-list/words");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("newword");
        Status status = git.status().call();
        return "Words list is updated!" + status.isClean();
    }
}
