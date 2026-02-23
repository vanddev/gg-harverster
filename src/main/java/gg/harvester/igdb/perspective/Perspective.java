package gg.harvester.igdb.perspective;

import gg.harvester.igdb.SimpleDTO;
import gg.harvester.igdb.SimpleEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_perspective")
public class Perspective extends SimpleEntity {

    public static Perspective parseDTO(SimpleDTO dto) {
        return SimpleEntity.parseDTO(dto, Perspective::new);
    }
}
