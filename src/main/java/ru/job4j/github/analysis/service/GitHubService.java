package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;

@Service
@AllArgsConstructor
public class GitHubService {

    private final GitHubRemote gitHubRemote;

    public List<Repository> fetchRepositories(String username) {
        return gitHubRemote.fetchRepositories(username);
    }

    public List<Commit> fetchCommits(String repository) {
        var parts = repository.split("/", 2);
        if (parts.length != 2) {
            return List.of();
        }
        return gitHubRemote.fetchCommits(parts[0], parts[1]);
    }

    public List<Commit> fetchCommits(String owner, String repository, String sha) {
        return gitHubRemote.fetchCommits(owner, repository, sha);
    }

}
