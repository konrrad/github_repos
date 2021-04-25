package com.example.allegro.domain;

import lombok.Data;

@Data
public class GithubLink
{
    public String url;
    public String rel;

    public GithubLink(String link)
    {
        parseLink(link);
    }

    private void parseLink(String link) {
        String[] parts=link.split(";");
        String bad_rel=parts[1];
        rel=bad_rel.split("=")[1].replaceAll("\"","");
        String bad_url=parts[0];
        url=bad_url.substring(bad_url.indexOf("<")+1,bad_url.indexOf(">"));
    }


    public boolean isNext()
    {
        return rel.equals("next");
    }

}
