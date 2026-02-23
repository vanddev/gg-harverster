package gg.harvester.igdb.gamemode;

import gg.harvester.igdb.SimpleDTO;
import gg.harvester.igdb.SimpleEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "gamemode")
public class Gamemode extends SimpleEntity {

    public static Gamemode parseDTO(SimpleDTO dto) {
        return SimpleEntity.parseDTO(dto, Gamemode::new);
    }
}
