package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ScheduledTasks {

    private final RepositoryService repositoryService;

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() {
        repositoryService.fetchCommits();
    }

}
