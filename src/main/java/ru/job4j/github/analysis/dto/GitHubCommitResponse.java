package ru.job4j.github.analysis.dto;

import lombok.Data;

@Data
public class GitHubCommitResponse {

    private CommitData commit;

    @Data
    public static class CommitData {

        private String message;

        private AuthorData author;
    }

    @Data
    public static class AuthorData {

        private String name;

        private String date;
    }

}
