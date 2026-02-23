package gg.harvester.igdb;

public record SimpleDTO(
        Integer id,
        String name
) implements IdentifiedResource {

    @Override
    public Integer getId() {
        return id;
    }
}
