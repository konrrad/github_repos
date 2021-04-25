package com.example.allegro;

import com.example.allegro.service.github.GithubRepo;
import com.example.allegro.service.github.GithubReposService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(GithubReposService.class)
public class GithubReposServiceTests {

    private final String user = "example";
    private final String requestAddress = String.format("https://api.github.com/users/%s/repos", user);
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private GithubReposService githubReposService;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    public void shouldObtainRepos() throws JsonProcessingException {
        //given
        List<GithubRepo> githubRepos = Arrays.asList(new GithubRepo("repo1", "repo1_full", 4), new GithubRepo("repo2", "repo2_full", 3));

        mockRestServiceServer.expect(ExpectedCount.once(),
                requestTo(this.requestAddress))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(githubRepos)));

        //when
        Optional<List<GithubRepo>> obtainedRepos = githubReposService.getReposForUser(user);
        //then
        mockRestServiceServer.verify();
        assertEquals(Optional.of(githubRepos), obtainedRepos);
    }

    @Test
    public void shouldRespondToError() {
        //given
        mockRestServiceServer.expect(ExpectedCount.once(),
                requestTo(this.requestAddress)).andRespond(withStatus(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON).body("Cannot find user"));

        //when
        var response = githubReposService.getReposForUser(user);
        //then
        mockRestServiceServer.verify();
        assertEquals(Optional.empty(), response);
    }

    @Test
    public void shouldRespondToEmpty() throws JsonProcessingException {
        //given
        mockRestServiceServer.expect(ExpectedCount.once(),
                requestTo(this.requestAddress)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(Collections.emptyList())));

        //when
        var response = githubReposService.getReposForUser(user);
        //then
        mockRestServiceServer.verify();
        assertEquals(Optional.of(Collections.emptyList()), response);
    }

    @Test
    public void shouldGetForMultiplePages() throws JsonProcessingException {
        //given
        HttpHeaders headers = new HttpHeaders();
        headers.set("Link", "<https://api.github.com/user/69631/repos?page=3>; rel=\"next\"");
        mockRestServiceServer.expect(ExpectedCount.once(), method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(headers).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(Collections.emptyList())));
        mockRestServiceServer.expect(ExpectedCount.once(), method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK));

        //when
        var response = githubReposService.getReposForUser("example");

        //then
        mockRestServiceServer.verify();

    }

    @Test
    public void shouldGetFromMultiplePages() throws JsonProcessingException {
        //given
        List<GithubRepo> firstPageGithubRepos = Arrays.asList(new GithubRepo("repo1", "repo1_full", 4)
                , new GithubRepo("repo2", "repo2_full", 3));
        List<GithubRepo> secondPageGithubRepos = Arrays.asList(new GithubRepo("repo1", "repo1_full", 4)
                , new GithubRepo("repo2", "repo2_full", 3));
        var result = Stream.concat(firstPageGithubRepos.stream(), secondPageGithubRepos.stream()).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Link", "<https://api.github.com/user/69631/repos?page=3>; rel=\"next\"");

        mockRestServiceServer.expect(ExpectedCount.once(), method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK).headers(headers).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(firstPageGithubRepos)));
        mockRestServiceServer.expect(ExpectedCount.once(), method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsString(secondPageGithubRepos)));


        //when
        var response = githubReposService.getReposForUser("example");

        //then
        mockRestServiceServer.verify();
        assertTrue(response.isPresent());
        assertEquals(response.get(), result);
    }

}
