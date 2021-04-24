package com.example.allegro.api;

import com.example.allegro.domain.GithubCountingService;
import com.example.allegro.domain.GithubRepo;
import com.example.allegro.domain.ReposService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/github/repos")
public class GithubController {
    private final ReposService reposService;
    private final GithubCountingService githubCountingService;

    @Autowired
    public GithubController(ReposService reposService, GithubCountingService githubCountingService)
    {
        this.reposService=reposService;
        this.githubCountingService=githubCountingService;

    }

    @GetMapping("{username}")
    public ResponseEntity<Iterable<GithubRepo>> getGithubReposForUser(@PathVariable String username) {
        var repos= reposService.getReposForUser(username);
        return ResponseEntity.status(repos.isPresent()?HttpStatus.OK:HttpStatus.SERVICE_UNAVAILABLE).body(repos.orElse(new ArrayList<>()));
    }

    @GetMapping("/stars/{username}")
    public ResponseEntity<Integer> getStarsForUser(@PathVariable String username)
    {
        var repos=reposService.getReposForUser(username);
        if(repos.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(githubCountingService.countStargazers(repos.get()));


    }


}
