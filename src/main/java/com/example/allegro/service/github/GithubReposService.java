package com.example.allegro.service.github;


import com.example.allegro.service.ReposService;
import com.example.allegro.service.RestTemplateResponseErrorHandler;
import com.example.allegro.service.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class GithubReposService implements ReposService {
    public static final String GITHUB_API = "https://api.github.com";
    private static final String LINK_HEADER = "Link";

    private final RestTemplate restTemplate;

    @Autowired
    public GithubReposService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.errorHandler(new RestTemplateResponseErrorHandler()).build();
    }

    public Optional<List<GithubRepo>> getReposForUser(String username) {
        final String address = getAddressForUser(username);
        List<GithubRepo[]> githubReposArrays = new ArrayList<>();
        try {
            ResponseEntity<GithubRepo[]> responseEntity = restTemplate.getForEntity(address, GithubRepo[].class);
            githubReposArrays.add(responseEntity.getBody());
            while (hasNextPage(responseEntity)) {
                var nextPageURL = getLinkForNextPage(responseEntity);
                System.out.println(nextPageURL);
                responseEntity = restTemplate.getForEntity(nextPageURL, GithubRepo[].class);
                githubReposArrays.add(responseEntity.getBody());
            }
//            return Optional.of(githubReposArrays.stream().flatMap(Arrays::stream).collect(Collectors.toList()));
//            return githubReposArrays.stream().map(Optional::ofNullable).flatMap().collect(Collectors.toList());
            return Optional.of(githubReposArrays.stream().filter(Objects::nonNull).flatMap(Arrays::stream).collect(Collectors.toList()));
        } catch (UserNotFoundException e) {
            return Optional.empty();
        }
    }

    private boolean hasNextPage(ResponseEntity<GithubRepo[]> responseEntity) {

        return mapToGithubLink(responseEntity)
                .anyMatch(GithubLink::isNext);
    }

    private String getLinkForNextPage(ResponseEntity<GithubRepo[]> responseEntity) {
        return mapToGithubLink(responseEntity)
                .filter(GithubLink::isNext)
                .map(link -> link.url)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Link should contain a link to the next page."));
    }

    private Stream<GithubLink> mapToGithubLink(ResponseEntity<GithubRepo[]> responseEntity) {
        final HttpHeaders headers = responseEntity.getHeaders();
        if (headers.get(LINK_HEADER) == null) return Stream.empty();
        return headers.get(LINK_HEADER)
                .stream()
                .flatMap(str -> Arrays.stream(str.split(",")))
                .map(GithubLink::new);
    }

    private String getAddressForUser(String username) {
        return String.format("%s/users/%s/repos", GITHUB_API, username);
    }


}
