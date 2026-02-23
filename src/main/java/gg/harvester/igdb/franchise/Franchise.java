package gg.harvester.igdb.franchise;

import gg.harvester.igdb.SimpleDTO;
import gg.harvester.igdb.SimpleEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "franchise")
public class Franchise extends SimpleEntity {

    public static Franchise parseDTO(SimpleDTO dto) {
        return SimpleEntity.parseDTO(dto, Franchise::new);
    }
}
