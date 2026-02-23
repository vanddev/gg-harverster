package gg.harvester.igdb.gamestatus;

import gg.harvester.igdb.gametype.Gametype;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class GamestatusRepository implements PanacheRepository<Gamestatus> {

    public Optional<Gamestatus> findByStatus(String status) {
        return find("status = ?1", status).singleResultOptional();
    }
}
