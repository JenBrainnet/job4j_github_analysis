package ru.job4j.github.analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.github.analysis.model.Repository;

import java.util.Optional;

public interface RepositoryStore extends JpaRepository<Repository, Long> {

    Optional<Repository> findByName(String name);

    Optional<Repository> findByUrl(String url);

}
