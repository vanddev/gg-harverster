package gg.harvester.igdb.platform;

import com.fasterxml.jackson.annotation.JsonProperty;
import gg.harvester.igdb.IdentifiedResource;
import gg.harvester.igdb.ImageDTO;

public record PlatformDTO(
        Integer id,
        String name,
        String abbreviation,

        @JsonProperty("alternative_name") String alternativeName,

        @JsonProperty("platform_logo") ImageDTO logo
) implements IdentifiedResource {
    @Override
    public Integer getId() {
        return id;
    }
}
