package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ScheduledTasksTest {

    @Test
    void whenFetchCommitsThenDelegateToRepositoryService() {
        var repositoryService = mock(RepositoryService.class);
        var scheduledTasks = new ScheduledTasks(repositoryService);
        scheduledTasks.fetchCommits();
        verify(repositoryService).fetchCommits();
    }

}
