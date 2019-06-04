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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class GitServiceImpl implements GitService {
    private static final String WORDS_GIT_ENV = "words.git.branch";
    private static final String GIT_URI = "git@github.com:aeternas/SwadeshNess-words-list.git";
    private static final String WORDS_REPO_DIR = "words-list";
    private static final String WORDS_FILE = "/words";
    private static final String REFS_PREFIX= "refs/heads/";
    private static final String PRIVATE_KEY_PATH="/root/.ssh/id_rsa";

    private Logger logger = LoggerFactory.getLogger(GitServiceImpl.class);

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    private ExecutorService executor;

    private ExecutorService getTaskExecutor() {
        if (executor == null) {
            logger.debug("Creating single thread executor");
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    @Override
    public void setEnv(Environment env) {
        this.env = env;
    }

    private Environment env;

    @Override
    public void pushAll(String message) throws IOException, GitAPIException, ExecutionException, InterruptedException {
        ExecutorService executor = getTaskExecutor();
        logger.debug("Executor retrieved");
        PushTask task = new PushTask();
        task.setMessage(message);
        if (executor != null) {
            logger.debug("Submitting task");
            executor.submit(task).get();
        }
    }

    private class PushTask implements Callable<Void> {
        private String message;

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public Void call() throws Exception {
            Git git = getRepo();
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(WORDS_REPO_DIR + WORDS_FILE, true));
            out.newLine();
            out.write(message);
            out.close();
            git.commit().setAll(true).setMessage("Updated words list with word " + message).call();
            logger.debug("Commit made");

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
            return null;
        }
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
        logger.debug("Repo is cloned");
    }

    private Git getRepo() throws IOException, GitAPIException {
        String branch = env.getProperty(WORDS_GIT_ENV);
        File file = new File(WORDS_REPO_DIR);
        Git git;
        if (file.exists()) {
            logger.debug("Repo exists, opening");
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
            logger.debug("Repo doesn't exist, cloning...");
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
                defaultJSch.addIdentity(PRIVATE_KEY_PATH);
                return defaultJSch;
            }
        };
    }
}
