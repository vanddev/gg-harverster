package gg.harvester.igdb.agerating;

import com.fasterxml.jackson.annotation.JsonProperty;
import gg.harvester.igdb.IdentifiedResource;
import gg.harvester.igdb.SimpleDTO;

import java.util.List;

public record AgeRatingDTO (
        Integer id,
        SimpleDTO organization,
        @JsonProperty("rating_category")
        RatingDTO rating,

        @JsonProperty("rating_content_descriptions")
        List<ContentDescriptionDTO> descriptions
) implements IdentifiedResource {
    @Override
    public Integer getId() {
        return id;
    }
}
