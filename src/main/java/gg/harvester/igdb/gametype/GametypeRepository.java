package gg.harvester.igdb.gametype;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class GametypeRepository implements PanacheRepository<Gametype> {

    public Optional<Gametype> findByType(String type) {
        return find("type = ?1", type).singleResultOptional();
    }
}
