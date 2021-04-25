package com.example.allegro.service;

import com.example.allegro.service.github.GithubRepo;

import java.util.List;
import java.util.Optional;

public interface ReposService {
    Optional<List<GithubRepo>> getReposForUser(String username);
}
