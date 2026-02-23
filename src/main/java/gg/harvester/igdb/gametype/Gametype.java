package gg.harvester.igdb.gametype;

import gg.harvester.igdb.BaseEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name="gametype")
public class Gametype extends BaseEntity {

    public static List<String> fields = List.of("type");

    public String type;

    public static Gametype parseDTO(GameTypesDTO dto) {
        Gametype gametype = new Gametype();
        gametype.id = dto.id();
        gametype.type = dto.type();

        return gametype;
    }
}
