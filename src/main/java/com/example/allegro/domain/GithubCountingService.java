package com.example.allegro.domain;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubCountingService {
    public int countStargazers(List<GithubRepo> repos)
    {
        return repos.stream().map(GithubRepo::getStargazers_count).reduce(0, Integer::sum);
    }
}
