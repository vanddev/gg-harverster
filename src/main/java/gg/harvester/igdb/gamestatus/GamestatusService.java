package gg.harvester.igdb.gamestatus;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class GamestatusService {

    private final GamestatusClient gamestatusClient;
    private final GamestatusRepository repository;

    public GamestatusService(
            @RestClient GamestatusClient gamestatusClient,
            GamestatusRepository repository
    ) {
        this.gamestatusClient = gamestatusClient;
        this.repository = repository;
    }

    @Transactional
    @CacheResult(cacheName = "released-status")
    public Gamestatus findReleasedStatus() {
        final String RELEASED_STATUS_NAME = "Released";
        var result = repository.findByStatus(RELEASED_STATUS_NAME);

        if (result.isPresent()) {
            return result.get();
        }

        var body = String.format("fields *; where status=\"%s\";", RELEASED_STATUS_NAME);
        var response = gamestatusClient.fetchGameStatus(body);

        if (response.isEmpty()) {
            return null;
        }

        var status = Gamestatus.parseDTO(response.getFirst());
        repository.persist(status);
        return status;
    }
}
