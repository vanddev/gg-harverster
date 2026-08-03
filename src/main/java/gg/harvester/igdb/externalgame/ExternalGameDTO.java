package gg.harvester.igdb.externalgame;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExternalGameDTO(
    String uid,

    @JsonProperty("external_game_source")
    Integer externalGameSource
) {

}
