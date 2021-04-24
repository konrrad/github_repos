package com.example.allegro.domain;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
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
        List<GithubRepo[]> githubReposArrays=new ArrayList<>();
        try {
            ResponseEntity<GithubRepo[]> responseEntity=restTemplate.getForEntity(address, GithubRepo[].class);
            githubReposArrays.add(responseEntity.getBody());
            while (hasNextPage(responseEntity))
            {
                var link=getLinkForNextPage(responseEntity);
                System.out.println(link);
                responseEntity=restTemplate.getForEntity(link.url,GithubRepo[].class);
                githubReposArrays.add(responseEntity.getBody());
            }
            return Optional.of(githubReposArrays.stream().flatMap(Arrays::stream).collect(Collectors.toList()));
        } catch (UserNotFoundException e) {
            return Optional.empty();
        }
    }

    private boolean hasNextPage(ResponseEntity<GithubRepo[]> responseEntity)
    {
        return responseEntity.getHeaders().containsKey("Link")&&
                responseEntity.getHeaders().get("Link")!=null&&
                Objects.requireNonNull(responseEntity.getHeaders().get("Link")).stream().anyMatch(s->s.contains("next"));
    }

    private GithubLink getLinkForNextPage(ResponseEntity<GithubRepo[]> responseEntity)
    {
        return responseEntity.getHeaders().get("Link")
                .stream()
                .flatMap(str-> Arrays.stream(str.split(",")))
                .map(GithubLink::new)
                .filter(GithubLink::isNext)
                .findFirst().get();
    }

    private String getAddressForUser(String username) {
        return String.format("%s/users/%s/repos", GITHUB_API, username);
    }


}
