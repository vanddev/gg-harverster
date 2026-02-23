package gg.harvester.igdb.agerating;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentDescriptionDTO(@JsonProperty("description") String description) { }
