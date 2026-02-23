package gg.harvester.igdb.platform;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class PlatformService {

    private final PlatformClient platformClient;
    private final PlatformRepository repository;

    public PlatformService(
            @RestClient PlatformClient platformClient,
            PlatformRepository repository
    ) {
        this.platformClient = platformClient;
        this.repository = repository;
    }

    @Transactional
    public Platform findByName(String platformName) {
        return repository.findByName(platformName).orElseGet(() -> this.fetchPlatform(platformName));
    }

    @Transactional
    public Platform fetchPlatform(String platformName) {

        Log.infof("Querying IGDB for platform: %s", platformName);
        var filter = String.format("name = \"%1$s\" | abbreviation = \"%1$s\" | alternative_name = \"%1$s\"", platformName);
        var fieldsStr = String.join(", ", Platform.fields);
        var body = String.format("fields %s; where %s;", fieldsStr, filter);
        var platforms = platformClient.fetchPlatforms(body);
        if (platforms.isEmpty()) return null;
        var newPlatform = Platform.parseDTO(platforms.getFirst());
        repository.persist(newPlatform);
        return newPlatform;
    }
}