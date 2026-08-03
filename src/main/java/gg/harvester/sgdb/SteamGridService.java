package gg.harvester.sgdb;

import gg.harvester.steam.SteamAssetService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class SteamGridService {

    private final SteamGridDBClient sgdb;
    private final SteamAssetService steamAssetService;
    private final String QUERY_TYPES = "static";
    private final boolean QUERY_NSFW = false;

    public SteamGridService(
      @RestClient SteamGridDBClient sgdb,
      SteamAssetService steamAssets
    ) {
        this.sgdb = sgdb;
        this.steamAssetService = steamAssets;
    }

    public SteamGridGameAsset GetGameAsset(Integer gameId, String gameName, Integer steamAppId) {
        return new SteamGridGameAsset(
          gameId,
          GetGameResource(gameName, steamAppId, AssetType.COVER),
          GetGameResource(gameName, steamAppId, AssetType.HERO),
          GetGameResource(gameName, steamAppId, AssetType.LOGO)
        );
    }

    private String GetGameResource(String gameName, Integer steamAppId, AssetType type) {

        SteamGridGame game = null;

        if (steamAppId != null) {
            String official = steamAssetService.Get(steamAppId, type);

            if (official != null) {
              return official;
            }

          Log.debugf("Official Steam %s Asset doesn't find for game %s", type, steamAppId);
          try {
            var gameResponse = sgdb.getBySteamId(steamAppId.toString());
            Log.debugf("Found the game in sgdb %s", gameResponse);
            game = gameResponse.success ? gameResponse.data : null;
          } catch (NotFoundException e) {
            Log.debugf("Not Found the game in sgdb %s", steamAppId);
          }
        }

        if (game == null) {

            var search = sgdb.search(gameName);

            if (search == null || search.data == null || search.data.isEmpty()) {
              Log.debugf("Game %s not found on steamgrid API", gameName);
              return null;
            }

            game = search.data.getFirst();
        }
        
        var sgdbAsset = switch (type) {
          case COVER -> sgdb.covers(game.id, QUERY_TYPES, QUERY_NSFW);
          case LOGO -> sgdb.logos(game.id, QUERY_TYPES, QUERY_NSFW);
          case HERO -> sgdb.heroes(game.id, QUERY_TYPES, QUERY_NSFW);
        };

        Log.debugf("Found asset %s on SGDB with response %s", type, sgdbAsset);

        if (sgdbAsset.success && !sgdbAsset.data.isEmpty()) {
          return sgdbAsset.data.getFirst().url;
        }

        return null;
    }
}
