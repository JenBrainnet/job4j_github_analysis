package ru.job4j.github.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.job4j.github.analysis.model.Commit;

import java.util.List;

@Data
@AllArgsConstructor
public class RepositoryCommits {

    private String repository;

    private List<Commit> commits;

}
