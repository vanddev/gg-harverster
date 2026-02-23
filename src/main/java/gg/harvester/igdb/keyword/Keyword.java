package gg.harvester.igdb.keyword;

import gg.harvester.igdb.SimpleDTO;
import gg.harvester.igdb.SimpleEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "keyword")
public class Keyword extends SimpleEntity {

    public static Keyword parseDTO(SimpleDTO dto) {
        return SimpleEntity.parseDTO(dto, Keyword::new);
    }
}
