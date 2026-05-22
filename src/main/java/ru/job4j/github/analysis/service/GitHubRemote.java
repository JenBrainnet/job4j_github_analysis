package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.job4j.github.analysis.dto.GitHubCommitResponse;
import ru.job4j.github.analysis.dto.GitHubRepositoryResponse;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
public class GitHubRemote {

    private static final String GITHUB_API = "https://api.github.com";

    private final RestTemplate restTemplate;

    public List<Repository> fetchRepositories(String username) {
        var url = GITHUB_API + "/users/{username}/repos";
        var response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GitHubRepositoryResponse>>() { },
                username
        );
        return response.getBody() == null ? List.of() : response.getBody().stream()
                .map(this::mapRepository)
                .toList();
    }

    public List<Commit> fetchCommits(String owner, String repoName) {
        return fetchCommits(owner, repoName, null);
    }

    public List<Commit> fetchCommits(String owner, String repoName, String sha) {
        var url = UriComponentsBuilder
                .fromHttpUrl(GITHUB_API + "/repos/{owner}/{repo}/commits")
                .queryParamIfPresent("sha", sha == null || sha.isBlank()
                        ? java.util.Optional.empty()
                        : java.util.Optional.of(sha))
                .buildAndExpand(owner, repoName)
                .toUriString();
        var response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<GitHubCommitResponse>>() { }
        );
        return response.getBody() == null ? List.of() : response.getBody().stream()
                .map(this::mapCommit)
                .toList();
    }

    private Repository mapRepository(GitHubRepositoryResponse source) {
        var repository = new Repository();
        repository.setName(source.getName());
        repository.setUrl(source.getHtmlUrl());
        return repository;
    }

    private Commit mapCommit(GitHubCommitResponse source) {
        var commit = new Commit();
        if (source.getCommit() == null) {
            return commit;
        }
        commit.setMessage(source.getCommit().getMessage());
        if (source.getCommit().getAuthor() != null) {
            commit.setAuthor(source.getCommit().getAuthor().getName());
            commit.setDate(parseDate(source.getCommit().getAuthor().getDate()));
        }
        return commit;
    }

    private LocalDateTime parseDate(String date) {
        return date == null ? null : LocalDateTime.ofInstant(Instant.parse(date), ZoneOffset.UTC);
    }

}
