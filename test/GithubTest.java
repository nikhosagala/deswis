import com.deswis.utils.Config;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhosagala on 02/07/2017.
 */
public class GithubTest {
    static GithubTest instance = null;

    static GithubTest getInstance() {
        if (instance == null) {
            instance = new GithubTest();
        }
        return instance;
    }

    public void testGithub() {
        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(Config.getInstance().getGithubApiKey());
        Map<Repository, Commit> map = new HashMap<>();
        RepositoryService repositoryServiceservice = new RepositoryService(client);
        CommitService commitService = new CommitService(client);
        try {
            for (Repository repo : repositoryServiceservice.getRepositories()) {
                map.put(repo, commitService.getCommits(repo).get(0).getCommit());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.forEach((k, v) ->
                System.out.println(k.getName() + " " + Config.getInstance().getStringWithDay(v.getAuthor().getDate()))
        );
    }

    public static void main(String args[]) {
        getInstance().testGithub();
    }
}
