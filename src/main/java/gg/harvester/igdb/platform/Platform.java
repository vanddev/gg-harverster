package gg.harvester.igdb.platform;

import gg.harvester.igdb.BaseEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "platform")
public class Platform extends BaseEntity {

    public static List<String> fields = List.of("name", "abbreviation", "alternative_name", "platform_logo.image_id");

    public String name;
    public String abbreviation;
    public String logo;

    @Column(name = "alternative_name")
    public String alternativeName;

    public static Platform parseDTO(PlatformDTO dto) {
        var platform = new Platform();
        platform.id = dto.id();
        platform.name = dto.name();
        platform.abbreviation = dto.abbreviation();
        platform.alternativeName = dto.alternativeName();
        platform.logo = dto.logo() != null ? dto.logo().imageId() : null;
        return platform;
    }
}
