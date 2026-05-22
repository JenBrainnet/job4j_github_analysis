package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.CommitStore;
import ru.job4j.github.analysis.repository.RepositoryStore;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RepositoryService {

    private final RepositoryStore repositoryStore;

    private final CommitStore commitStore;

    private final GitHubRemote gitHubRemote;

    @Async
    @Transactional
    public void create(Repository repository) {
        saveRepositoryIfNew(repository);
    }

    @Async
    @Transactional
    public void create(String username) {
        gitHubRemote.fetchRepositories(username).forEach(this::saveRepositoryIfNew);
    }

    @Transactional(readOnly = true)
    public List<Repository> findAll() {
        return repositoryStore.findAll();
    }

    @Transactional(readOnly = true)
    public List<RepositoryCommits> findCommitsByRepositoryName(String name) {
        return repositoryStore.findByName(name)
                .map(repository -> List.of(new RepositoryCommits(
                        repository.getName(),
                        commitStore.findByRepositoryName(repository.getName())
                )))
                .orElse(List.of());
    }

    @Transactional
    public void fetchCommits() {
        repositoryStore.findAll().forEach(this::fetchCommits);
    }

    private void fetchCommits(Repository repository) {
        getOwner(repository).ifPresent(owner -> gitHubRemote
                .fetchCommits(owner, repository.getName())
                .stream()
                .filter(this::isValid)
                .filter(commit -> isNew(repository, commit))
                .forEach(commit -> saveCommit(repository, commit)));
    }

    private void saveRepositoryIfNew(Repository repository) {
        if (repository.getUrl() == null || repository.getUrl().isBlank()) {
            return;
        }
        repositoryStore.findByUrl(repository.getUrl()).orElseGet(() -> repositoryStore.save(repository));
    }

    private void saveCommit(Repository repository, Commit commit) {
        commit.setRepository(repository);
        commitStore.save(commit);
    }

    private boolean isNew(Repository repository, Commit commit) {
        return !commitStore.existsByRepositoryAndMessageAndAuthorAndDate(
                repository,
                commit.getMessage(),
                commit.getAuthor(),
                commit.getDate()
        );
    }

    private boolean isValid(Commit commit) {
        return commit.getMessage() != null
                && commit.getAuthor() != null
                && commit.getDate() != null;
    }

    private Optional<String> getOwner(Repository repository) {
        try {
            var path = URI.create(repository.getUrl()).getPath();
            var parts = path.split("/");
            return parts.length > 1 ? Optional.of(parts[1]) : Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
