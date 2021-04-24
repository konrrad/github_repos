package com.example.allegro.domain;

import java.util.List;
import java.util.Optional;

public interface ReposService {
    Optional<List<GithubRepo>> getReposForUser(String username);
}
