package gg.harvester.igdb.gametype;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GametypeService {

    private final GametypeRepository repository;

    public GametypeService(GametypeRepository repository) {
        this.repository = repository;

    }

    @Transactional
    @CacheResult(cacheName = "edition-type")
    public Gametype findEditionType() {
        final String EDITION_TYPE_NAME = "Edition";
        var result = repository.findByType(EDITION_TYPE_NAME);

        if (result.isPresent()) {
            return result.get();
        }

        var edition = new Gametype();
        edition.id = 20;
        edition.type = EDITION_TYPE_NAME;
        repository.persist(edition);
        return edition;
    }
}
