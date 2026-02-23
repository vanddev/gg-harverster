package gg.harvester.igdb.gametype;

import gg.harvester.igdb.IdentifiedResource;

public record GameTypesDTO (
        Integer id,
        String type
) implements IdentifiedResource {
    @Override
    public Integer getId() {
        return id;
    }
}
