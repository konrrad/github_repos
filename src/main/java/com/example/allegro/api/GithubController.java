package com.example.allegro.api;

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
    ReposService reposService;

    @Autowired
    public GithubController(ReposService reposService)
    {
        this.reposService=reposService;
    }

    @GetMapping("{username}")
    public ResponseEntity<Iterable<GithubRepo>> getGithubReposForUser(@PathVariable String username) {
        var repos= reposService.getReposForUser(username);
        return ResponseEntity.status(repos.isPresent()?HttpStatus.OK:HttpStatus.SERVICE_UNAVAILABLE).body(repos.orElse(new ArrayList<>()));
    }

}
