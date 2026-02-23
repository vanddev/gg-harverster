package gg.harvester.igdb.gamerelationship;

import gg.harvester.igdb.BaseEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class GameRelationship extends BaseEntity {

    public Integer mainGameId;
    public Integer relatedGameId;

    @Enumerated(EnumType.STRING)
    public GameRelationshipType relationshipType;
}
