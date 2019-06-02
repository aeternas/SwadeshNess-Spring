package jp.ivan.swadeshness.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitServiceImpl implements GitService {

    private static final String WORDS_GIT_ENV = "words.git.branch";
    private static final String GIT_URI = "git@github.com:aeternas/SwadeshNess-words-list.git";
    private static final String WORDS_REPO_DIR = "words-list";
    private static final String WORDS_FILE = "/words";
    private static final String REFS_PREFIX= "refs/heads/";

    @Override
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    private Environment env;

    @Override
    public void pushAll(String message) throws IOException, GitAPIException {
        Git git = getRepo();
        BufferedWriter out = new BufferedWriter(
                new FileWriter(WORDS_REPO_DIR + WORDS_FILE, true));
        out.newLine();
        out.write(message);
        out.close();
        git.commit().setAll(true).setMessage("Updated words list with word " + message).call();

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
    }

    private void cloneRepo() throws GitAPIException {
        String branch = env.getProperty(WORDS_GIT_ENV);
        CloneCommand cloneCommand = Git
                .cloneRepository()
                .setURI( GIT_URI )
                .setCloneAllBranches( true )
                .setBranch(REFS_PREFIX + branch)
                .setDirectory(new File(WORDS_REPO_DIR))
                .setTransportConfigCallback(new TransportConfigCallback() {
                    @Override
                    public void configure(Transport transport) {
                        SshTransport sshTransport = ( SshTransport )transport;
                        sshTransport.setSshSessionFactory( getSessionFactory() );
                    }
                });
        cloneCommand.call();
    }

    private Git getRepo() throws IOException, GitAPIException {
        String branch = env.getProperty(WORDS_GIT_ENV);
        File file = new File(WORDS_REPO_DIR);
        Git git;
        if (file.exists()) {
            git = Git.open(file);
            git.checkout().setName(REFS_PREFIX + branch).call();
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
            cloneRepo();
            git = Git.open(file);
            git.checkout().setName(branch).call();
        }
        return git;
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
}
