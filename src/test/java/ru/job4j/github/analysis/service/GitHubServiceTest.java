package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.Test;
import ru.job4j.github.analysis.model.Commit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class GitHubServiceTest {

    private final GitHubRemote gitHubRemote = mock(GitHubRemote.class);

    private final GitHubService gitHubService = new GitHubService(gitHubRemote);

    @Test
    void whenFetchCommitsWithOwnerAndRepositoryThenDelegateToRemote() {
        var commit = new Commit();
        when(gitHubRemote.fetchCommits("JenBrainnet", "job4j_github_analysis")).thenReturn(List.of(commit));
        var result = gitHubService.fetchCommits("JenBrainnet/job4j_github_analysis");
        assertThat(result).containsExactly(commit);
        verify(gitHubRemote).fetchCommits("JenBrainnet", "job4j_github_analysis");
    }

    @Test
    void whenFetchCommitsWithInvalidRepositoryThenReturnEmptyList() {
        var result = gitHubService.fetchCommits("job4j_github_analysis");
        assertThat(result).isEmpty();
        verifyNoInteractions(gitHubRemote);
    }

}
