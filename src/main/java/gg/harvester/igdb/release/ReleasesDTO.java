package gg.harvester.igdb.release;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReleasesDTO(
        Integer id,
        ReleaseStatusDTO status,
        Integer platform,
        Integer game,
        @JsonProperty("release_region")
        ReleaseRegionDTO region,
        Integer date
) { }
