package gg.harvester.igdb.gamestatus;

import gg.harvester.igdb.IdentifiedResource;

public record GameStatusDTO (
        Integer id,
        String status
) implements IdentifiedResource {

    @Override
    public Integer getId() {
        return id;
    }
}
