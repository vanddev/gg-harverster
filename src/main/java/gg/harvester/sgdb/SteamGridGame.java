package gg.harvester.sgdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamGridGame {
    public Integer id;
    public String name;
    public boolean verified;
    public List<String> types;

    @JsonProperty("release_date")
    public Integer releaseDate;
}
