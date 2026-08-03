package gg.harvester.sgdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SteamGridSearch {
    public Integer id;
    public String name;
    public boolean verified;
    public List<String> types;

    @JsonProperty("release_date")
    public Integer releaseDate;
}
