package gg.harvester.igdb.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import gg.harvester.igdb.ImageDTO;
import gg.harvester.igdb.SimpleDTO;
import gg.harvester.igdb.agerating.AgeRatingDTO;
import gg.harvester.igdb.gamestatus.GameStatusDTO;
import gg.harvester.igdb.gametype.GameTypesDTO;
import gg.harvester.igdb.platform.PlatformDTO;
import gg.harvester.igdb.release.ReleasesDTO;

import java.util.List;

public record GamesDTO(
        Integer id,
        String name,
        String url,
        ImageDTO cover,
        List<PlatformDTO> platforms,
        @JsonProperty("aggregated_rating")
        Double aggregatedRating,
        @JsonProperty("aggregated_rating_count")
        Integer aggregatedRatingCount,
        Double rating,
        @JsonProperty("rating_count")
        Integer ratingCount,
        @JsonProperty("total_rating")
        Double totalRating,
        @JsonProperty("total_rating_count")
        Integer totalRatingCount,

        @JsonProperty("version_parent")
        Integer versionParent,

        @JsonProperty("game_type")
        GameTypesDTO gameType,
        @JsonProperty("game_status")
        GameStatusDTO gameStatus,
        @JsonProperty("game_modes")
        List<SimpleDTO> gameModes,
        @JsonProperty("release_dates")
        List<ReleasesDTO> releases,
        @JsonProperty("age_ratings")
        List<AgeRatingDTO> ageRatings,
        List<SimpleDTO> genres,
        @JsonProperty("player_perspectives")
        List<SimpleDTO> perspectives,
        List<SimpleDTO> themes,
        List<SimpleDTO> keywords,
        List<SimpleDTO> franchises,
        @JsonProperty("franchise")
        SimpleDTO mainFranchise
) {

}
