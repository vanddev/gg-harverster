package gg.harvester.igdb;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.List;
import java.util.function.Supplier;

@MappedSuperclass
public class SimpleEntity extends BaseEntity {

    public static List<String> fields = List.of("id", "name");

    public String name;

    public static <T extends SimpleEntity> T parseDTO(SimpleDTO dto, Supplier<T> supplier) {
        var entity = supplier.get();
        entity.id = dto.id();
        entity.name = dto.name();
        return entity;
    }
}
