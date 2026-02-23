package gg.harvester.igdb.agerating;

import gg.harvester.igdb.BaseEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "age_rating")
public class AgeRating extends BaseEntity {

    public static List<String> fields = List.of("organization.name", "rating_category.rating", "rating_content_descriptions.description");

    public String organization;
    public String rating;
    @Column(name = "content_descriptions")
    public String description;

    public static AgeRating parseDTO(AgeRatingDTO dto) {
        AgeRating entity = new AgeRating();
        entity.id = dto.id();
        entity.organization = dto.organization().name();
        entity.rating = dto.rating().rating();
        if (dto.descriptions() != null && !dto.descriptions().isEmpty()) {
            List<String> descriptions = dto.descriptions().stream().map(ContentDescriptionDTO::description).sorted().toList();
            entity.description = String.join(", ", descriptions);
        }
        return entity;
    }
}
