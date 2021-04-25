package com.example.allegro.service.github;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonSerialize
public class GithubRepo {
    private String name;
    private String full_name;
    private int stargazers_count;
}
