package jp.ivan.swadeshness;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import com.jcraft.jsch.*;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

    @Autowired
    private Environment env;

    @RequestMapping(value = "/words/{word}", method = RequestMethod.PUT)
    public String index(@PathVariable String word) throws GitAPIException, IOException {
        String branch = env.getProperty("words.git.branch");
        File file = new File("./sw2");
        Git git;
        if (file.exists()) {
            git = Git.open(file);
            git.checkout().setName("refs/heads/" + branch).call();
            git.pull()
                    .setTransportConfigCallback(new TransportConfigCallback() {
                        @Override
                        public void configure(Transport transport) {
                            SshTransport sshTransport = ( SshTransport )transport;
                            sshTransport.setSshSessionFactory( getSessionFactory() );
                        }
                    })
                    .call();
        } else {
            git = createRepo();
            git.checkout().setName(branch).call();
        }
        if (git == null) {
            return "Error while creating git repo";
        }
        BufferedWriter out = new BufferedWriter(
                new FileWriter("./sw2/words/", true));
        out.newLine();
        out.write(word);
        out.close();
        git.commit().setAll(true).setMessage("Updated words list").call();

        PushCommand pushCommand = git
                .push()
                .setTransportConfigCallback( new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = ( SshTransport )transport;
                sshTransport.setSshSessionFactory( getSessionFactory() );
            }
        });

        pushCommand.call();

        return "Words list is updated with word";
    }

    private SshSessionFactory getSessionFactory() {
        return new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.addIdentity("/root/.ssh/id_rsa");
                return defaultJSch;
            }
        };
    }

    private Git createRepo() throws GitAPIException {
        String branch = env.getProperty("words.git.branch");
        CloneCommand cloneCommand = Git
                .cloneRepository()
                .setURI( "git@github.com:aeternas/SwadeshNess-words-list.git" )
                .setCloneAllBranches( true )
                .setBranch("refs/heads/" + branch)
                .setDirectory(new File("sw2"))
                .setTransportConfigCallback(new TransportConfigCallback() {
                    @Override
                    public void configure(Transport transport) {
                        SshTransport sshTransport = ( SshTransport )transport;
                        sshTransport.setSshSessionFactory( getSessionFactory() );
                    }
                });
        return cloneCommand.call();
    }
}
