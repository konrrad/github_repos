package com.example.allegro.domain;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GithubReposService implements ReposService {
    public static final String GITHUB_API = "https://api.github.com";

    private final RestTemplate restTemplate;

    @Autowired
    public GithubReposService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
    }

    public Optional<List<GithubRepo>> getReposForUser(String username) {
        final String address = getAddressForUser(username);
        try {
            ResponseEntity<GithubRepo[]> responseEntity = restTemplate.getForEntity(address, GithubRepo[].class);
            if (responseEntity.getStatusCode().isError() || responseEntity.getBody() == null) {
                return Optional.empty();
            }
            return Optional.of(Arrays.stream(responseEntity.getBody()).collect(Collectors.toList()));
        } catch (UserNotFoundException e)
        {
            return Optional.empty();
        }

    }

    private String getAddressForUser(String username) {
        return String.format("%s/users/%s/repos", GITHUB_API, username);
    }
}
