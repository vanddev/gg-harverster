package gg.harvester.igdb;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity extends PanacheEntityBase {

    @Id
    public Integer id;

    public Integer getId() {
        return id;
    }
}
