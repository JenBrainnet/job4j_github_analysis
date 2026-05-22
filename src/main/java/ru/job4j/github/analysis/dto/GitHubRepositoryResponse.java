package ru.job4j.github.analysis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubRepositoryResponse {

    private String name;

    @JsonProperty("html_url")
    private String htmlUrl;

}
