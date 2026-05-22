package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.github.analysis.model.Commit;

import java.util.List;

public interface CommitStore extends JpaRepository<Commit, Long> {

    List<Commit> findByRepositoryName(String repositoryName);

}
