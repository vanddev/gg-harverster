package gg.harvester.igdb.platform;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PlatformRepository implements PanacheRepository<Platform> {

    public Optional<Platform> findByName(String platformName) {
        PanacheQuery<Platform> q = Platform.find("name = ?1 or abbreviation = ?1 or alternativeName = ?1", platformName);
        return q.singleResultOptional();
    }

}
