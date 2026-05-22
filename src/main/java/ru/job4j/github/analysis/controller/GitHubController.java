package ru.job4j.github.analysis.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.github.analysis.dto.RepositoryCommits;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.RepositoryService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class GitHubController {

    private final RepositoryService repositoryService;

    @GetMapping("/repositories")
    public List<Repository> getAllRepositories() {
        return repositoryService.findAll();
    }

    @GetMapping("/commits/{name}")
    public List<RepositoryCommits> getCommits(@PathVariable(value = "name") String name) {
        return repositoryService.findCommitsByRepositoryName(name);
    }

    @PostMapping("/repository")
    public ResponseEntity<Void> create(@RequestBody Repository repository) {
        repositoryService.create(repository);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/repositories/{username}")
    public ResponseEntity<Void> create(@PathVariable String username) {
        repositoryService.create(username);
        return ResponseEntity.noContent().build();
    }

}
