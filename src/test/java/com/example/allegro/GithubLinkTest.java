package com.example.allegro;

import com.example.allegro.domain.GithubLink;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GithubLinkTest {
    @Test
    public void t()
    {
        var links="\t<https://api.github.com/user/69631/repos?page=1>; rel=\"prev\", <https://api.github.com/user/69631/repos?page=3>; rel=\"next\", <https://api.github.com/user/69631/repos?page=4>; rel=\"last\", <https://api.github.com/user/69631/repos?page=1>; rel=\"first\"";
        Arrays.stream(links.split(",")).map(GithubLink::new).collect(Collectors.toList());
    }

}
