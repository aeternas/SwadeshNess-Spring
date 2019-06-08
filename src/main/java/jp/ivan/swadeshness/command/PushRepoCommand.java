package jp.ivan.swadeshness.command;

import jp.ivan.swadeshness.service.GitServiceImpl;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.Callable;

public class PushRepoCommand implements Callable<Void> {
    public static final String WORDS_REPO_DIR = "words-list";
    private static final String WORDS_FILE = "/words";


    private Logger logger = LoggerFactory.getLogger(PushRepoCommand.class);

    private GitServiceImpl gitService;
    private String message;

    public PushRepoCommand(GitServiceImpl gitService) {
        this.gitService = gitService;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Void call() throws Exception {
        Git git = gitService.getRepo();
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
                        sshTransport.setSshSessionFactory( gitService.getSessionFactory() );
                    }
                });

        pushCommand.call();
        return null;
    }
}
