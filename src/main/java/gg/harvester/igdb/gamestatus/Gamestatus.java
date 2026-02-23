package gg.harvester.igdb.gamestatus;

import gg.harvester.igdb.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name="gamestatus")
public class Gamestatus extends BaseEntity {

    public static List<String> fields = List.of("status");

    public String status;

    public static Gamestatus parseDTO(GameStatusDTO dto) {
        Gamestatus gamestatus = new Gamestatus();
        gamestatus.id = dto.id();
        gamestatus.status = dto.status();

        return gamestatus;
    }
}
