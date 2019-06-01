package jp.ivan.swadeshness;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

@RestController
public class LanguagesUpdateController {

    @RequestMapping(value = "/words/{word}", method = RequestMethod.PUT)
    public String index(@PathVariable String word) throws GitAPIException, IOException {
        File file = new File("./sw2");
        Git git;
        if (file.exists()) {
            git = Git.open(file);
        } else {
            git = createRepo();
        }
        if (git == null) {
            return "Error while creating git repo";
        }
        BufferedWriter out = new BufferedWriter(
                new FileWriter("./sw2/words/", true));
        out.write(word);
        out.close();
        git.commit().setAll(true).setMessage("test commit").call();
        git.push().call();

        return "Words list is updated with word";
    }

    private Git createRepo() throws GitAPIException {
        return Git.cloneRepository()
                .setURI( "https://github.com/aeternas/SwadeshNess-words-list.git" )
                .setDirectory(new File("sw2"))
                .call();
    }
}
