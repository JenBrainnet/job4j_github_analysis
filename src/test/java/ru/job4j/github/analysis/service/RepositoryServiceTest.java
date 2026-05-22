package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.Test;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.CommitStore;
import ru.job4j.github.analysis.repository.RepositoryStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RepositoryServiceTest {

    private final RepositoryStore repositoryStore = mock(RepositoryStore.class);

    private final CommitStore commitStore = mock(CommitStore.class);

    private final GitHubRemote gitHubRemote = mock(GitHubRemote.class);

    private final RepositoryService repositoryService = new RepositoryService(
            repositoryStore,
            commitStore,
            gitHubRemote
    );

    @Test
    void whenCreateNewRepositoryThenSaveIt() {
        var repository = createRepository("job4j_github_analysis");
        when(repositoryStore.findByUrl(repository.getUrl())).thenReturn(Optional.empty());
        repositoryService.create(repository);
        verify(repositoryStore).save(repository);
    }

    @Test
    void whenCreateExistingRepositoryThenDoNotSaveItAgain() {
        var repository = createRepository("job4j_github_analysis");
        when(repositoryStore.findByUrl(repository.getUrl())).thenReturn(Optional.of(repository));
        repositoryService.create(repository);
        verify(repositoryStore, never()).save(repository);
    }

    @Test
    void whenCreateByUsernameThenFetchAndSaveRepositories() {
        var repository = createRepository("job4j_github_analysis");
        when(gitHubRemote.fetchRepositories("JenBrainnet")).thenReturn(List.of(repository));
        when(repositoryStore.findByUrl(repository.getUrl())).thenReturn(Optional.empty());
        repositoryService.create("JenBrainnet");
        verify(gitHubRemote).fetchRepositories("JenBrainnet");
        verify(repositoryStore).save(repository);
    }

    @Test
    void whenFindAllThenReturnRepositories() {
        var repository = createRepository("job4j_github_analysis");
        when(repositoryStore.findAll()).thenReturn(List.of(repository));
        var result = repositoryService.findAll();
        assertThat(result).containsExactly(repository);
    }

    @Test
    void whenFindCommitsByRepositoryNameThenReturnDto() {
        var repository = createRepository("job4j_github_analysis");
        var commit = createCommit();
        when(repositoryStore.findByName(repository.getName())).thenReturn(Optional.of(repository));
        when(commitStore.findByRepositoryName(repository.getName())).thenReturn(List.of(commit));
        var result = repositoryService.findCommitsByRepositoryName(repository.getName());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRepository()).isEqualTo(repository.getName());
        assertThat(result.get(0).getCommits()).containsExactly(commit);
    }

    @Test
    void whenFetchCommitsThenSaveOnlyNewValidCommits() {
        var repository = createRepository("job4j_github_analysis");
        var commit = createCommit();
        when(repositoryStore.findAll()).thenReturn(List.of(repository));
        when(gitHubRemote.fetchCommits("JenBrainnet", repository.getName())).thenReturn(List.of(commit));
        when(commitStore.existsByRepositoryAndMessageAndAuthorAndDate(
                repository,
                commit.getMessage(),
                commit.getAuthor(),
                commit.getDate()
        )).thenReturn(false);
        repositoryService.fetchCommits();
        verify(commitStore).save(commit);
        assertThat(commit.getRepository()).isEqualTo(repository);
    }

    private Repository createRepository(String name) {
        var repository = new Repository();
        repository.setName(name);
        repository.setUrl("https://github.com/JenBrainnet/" + name);
        return repository;
    }

    private Commit createCommit() {
        var commit = new Commit();
        commit.setMessage("Initial commit");
        commit.setAuthor("Jen");
        commit.setDate(LocalDateTime.of(2026, 5, 23, 0, 0));
        return commit;
    }

}
