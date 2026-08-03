package gg.harvester.igdb.game;

import gg.harvester.ConsoleProgressBar;
import gg.harvester.igdb.*;
import gg.harvester.igdb.agerating.AgeRating;
import gg.harvester.igdb.agerating.AgeRatingDTO;
import gg.harvester.igdb.franchise.Franchise;
import gg.harvester.igdb.gamemode.Gamemode;
import gg.harvester.igdb.gamestatus.GameStatusDTO;
import gg.harvester.igdb.gamestatus.Gamestatus;
import gg.harvester.igdb.gamestatus.GamestatusService;
import gg.harvester.igdb.gametype.GameTypesDTO;
import gg.harvester.igdb.gametype.Gametype;
import gg.harvester.igdb.gametype.GametypeService;
import gg.harvester.igdb.genre.Genre;
import gg.harvester.igdb.keyword.Keyword;
import gg.harvester.igdb.perspective.Perspective;
import gg.harvester.igdb.platform.Platform;
import gg.harvester.igdb.platform.PlatformDTO;
import gg.harvester.igdb.platform.PlatformService;
import gg.harvester.igdb.release.Release;
import gg.harvester.igdb.theme.Theme;
import gg.harvester.sgdb.SteamGridService;
import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class GameService {
    private final PlatformService platformService;
    private final GameClient gameClient;
    private final GametypeService gametypeService;
    private final GamestatusService gamestatusService;
    private final SteamGridService steamGridService;
    private final GameRepository repository;
    private final List<String> ALLOWED_GAME_TYPE = List.of(
        "Main Game",
        "Standalone Expansion",
        "Remake",
        "Remaster",
        "Expanded Game",
        "Port"
    );

    public GameService(
            @RestClient GameClient gameClient,
            GameRepository repository,
            PlatformService platformService,
            GametypeService gametypeService,
            GamestatusService gamestatusService,
            SteamGridService steamGridService) {
        this.platformService = platformService;
        this.gametypeService = gametypeService;
        this.gamestatusService = gamestatusService;
        this.steamGridService = steamGridService;
        this.gameClient = gameClient;
        this.repository = repository;
    }

    public void importGamesByPlatform(String platformName) {
        Log.infof("Importing games for platform %s", platformName);
        long start = System.currentTimeMillis();

        var platform = platformService.findByName(platformName);

        var total = fetchGameCountByPlatform(platform.id);

        var processed = 0;
        var added = 0;
        var skipped = 0;

        try(ConsoleProgressBar progressBar = new ConsoleProgressBar("Syncing games...", total)){

          while (processed < total) {
              var games = fetchGameByPlatform(platform.id, processed);

              if (games.isEmpty()) break;

              var gamesAllowed = filterGamesAllowed(games);

              skipped += games.size() - gamesAllowed.size();

              var persistedNewGames = syncGames(gamesAllowed);


              processed += games.size();
              added += persistedNewGames.size();
              skipped += gamesAllowed.size() - persistedNewGames.size();

              progressBar.stepBy(games.size());

              var pct = String.format("%.2f%%", ((double) processed / total) * 100);
              Log.infof("Synced %d / %d", processed, total);
          }
        } catch (IOException e ){
          Log.error("An error occurred", e);
          System.exit(1);
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.printf("Import completed: added=%d, skipped=%d, duration=%dm%n", added, skipped, TimeUnit.MILLISECONDS.toMinutes(elapsed));
    }

    private ArrayList<GamesDTO> filterGamesAllowed(List<GamesDTO> games) {
        var gamesAllowed = new ArrayList<GamesDTO>();
        for (var game : games) {
            if (ALLOWED_GAME_TYPE.contains(game.gameType().type()) && game.releases() != null && !game.releases().isEmpty()) {
                gamesAllowed.add(game);
            }
        }

        return gamesAllowed;
    }

    @Transactional
    public List<Game> syncGames(List<GamesDTO> dtos) {
        var dtoIds = dtos.stream().map(GamesDTO::id).toList();
        List<Integer> persistedIds = repository.findGameIds(dtoIds);

        Log.infof("Found %d games in database", persistedIds.size());

        if (persistedIds.size() == dtos.size()) return Collections.emptyList();

        List<GamesDTO> gamesToPersist = dtos.stream()
                .filter(dto -> !persistedIds.contains(dto.id())).toList();

        Log.infof("Processing %d games to be saved", gamesToPersist.size());


        Map<String, Map<Integer, ? extends BaseEntity>> persistedData = persistAllGameData(Panache.getEntityManager(), gamesToPersist);

        var games = buildGamesFromDTO(dtos, persistedData);
        loadMediaAssets(games);
        repository.persist(games);
        Log.infof("Persisted %d new Games entities", games.size());

        return games;

    }

    private List<Game> buildGamesFromDTO(
            List<GamesDTO> dtos,
            Map<String, Map<Integer, ? extends BaseEntity>> relatedData
    ) {
        List<Game> games = new ArrayList<>();

        for (GamesDTO dto : dtos) {
            Game game = Game.parseDTO(dto);

            game.setReleases(dto.releases().stream()
                    .filter(release -> release.date() != null)
                    .map(release -> Release.parseDTO(release, game))
                    .collect(Collectors.toSet()));

            // ------------------------------
            // COLLECTION RELATIONS
            // ------------------------------

            game.platforms = filterEntities(dto.platforms(), relatedData, Platform.class);
            game.franchises = filterEntities(dto.franchises(), relatedData, Franchise.class);
            game.genres = filterEntities(dto.genres(), relatedData, Genre.class);
            game.themes = filterEntities(dto.themes(), relatedData, Theme.class);
            game.gamemodes = filterEntities(dto.gameModes(), relatedData, Gamemode.class);
            game.perspectives = filterEntities(dto.perspectives(), relatedData, Perspective.class);
            game.ageRatings = filterEntities(dto.ageRatings(), relatedData, AgeRating.class);
            game.keywords = filterEntities(dto.keywords(), relatedData, Keyword.class);

            // ------------------------------
            // SINGLE-ENTITY RELATIONS
            // ------------------------------
            if (dto.gameStatus() == null) {
                game.gamestatus = gamestatusService.findReleasedStatus();
            } else {
                game.gamestatus = filterEntity(
                        dto.gameStatus(),
                        relatedData,
                        Gamestatus.class
                );
            }

            if (dto.versionParent() != null) {
                game.gametype = gametypeService.findEditionType();
            } else {
                game.gametype = filterEntity(dto.gameType(), relatedData, Gametype.class);
            }
            games.add(game);
        }

        return games;
    }

    @SuppressWarnings("unchecked")
    private <E extends BaseEntity> Set<E> filterEntities(List<? extends IdentifiedResource> entitiesTofind,
                                                         Map<String, Map<Integer, ? extends BaseEntity>> entities,
                                                         Class<E> entityClass) {
        if (entitiesTofind == null || entitiesTofind.isEmpty()) return Set.of();

        Map<Integer, E> persistedMap = (Map<Integer, E>) entities.get(entityClass.getSimpleName());
        if (persistedMap == null || persistedMap.isEmpty()) return Set.of();


        Set<Integer> ids = entitiesTofind.stream().map(IdentifiedResource::getId).collect(Collectors.toSet());
        return ids.stream().map(persistedMap::get).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private <E extends BaseEntity> E filterEntity(IdentifiedResource entityToFind,
                                               Map<String, Map<Integer, ? extends BaseEntity>> entities,
                                               Class<E> entityClass) {

        if (entityToFind == null) return null;

        return filterEntities(List.of(entityToFind), entities, entityClass).stream().findFirst().orElse(null);
    }

    private List<GamesDTO> fetchGameByPlatform(Integer platformId, int offset) {
        String fields = String.join(", ", Game.fields) +
                "," + concateFieldsAPICalypse("game_modes", SimpleEntity.fields) +
                "," + concateFieldsAPICalypse("game_status", Gamestatus.fields) +
                "," + concateFieldsAPICalypse("game_type", Gametype.fields) +
                "," + concateFieldsAPICalypse("platforms", Platform.fields) +
                "," + concateFieldsAPICalypse("release_dates", Release.fields) +
                "," + concateFieldsAPICalypse("age_ratings", AgeRating.fields) +
                "," + concateFieldsAPICalypse("player_perspectives", SimpleEntity.fields) +
                "," + concateFieldsAPICalypse("genres", SimpleEntity.fields) +
                "," + concateFieldsAPICalypse("themes", SimpleEntity.fields) +
                "," + concateFieldsAPICalypse("keywords", SimpleEntity.fields) +
                "," + concateFieldsAPICalypse("franchise", SimpleEntity.fields) +
                "," + concateFieldsAPICalypse("franchises", SimpleEntity.fields);
        int LIMIT = 100;
        var filter = String.format("platforms = (%d); limit %d; offset %d; sort id asc", platformId, LIMIT, offset);
        return gameClient.fetchGames(String.format("fields %s; where %s;", fields, filter));
    }

    public Integer fetchGameCountByPlatform(Integer platformId) {
        var filter = String.format("where platforms=(%d);", platformId);
        return gameClient.fetchGamesCount(filter).count();
    }

    private String concateFieldsAPICalypse(String sourceField, List<String> resourceFields) {
        return resourceFields.stream()
                .map(field -> sourceField + "." + field)
                .collect(Collectors.joining(", "));
    }

    private Map<String, Map<Integer,? extends BaseEntity>> persistAllGameData(EntityManager entityManager, Collection<GamesDTO> gamesToPersist) {
        // Prepare sets for unique values
        Set<PlatformDTO> platforms = new HashSet<>();
        Set<SimpleDTO> franchises = new HashSet<>();
        Set<GameTypesDTO> gameTypes = new HashSet<>();
        Set<SimpleDTO> gameModes = new HashSet<>();
        Set<GameStatusDTO> gameStatuses = new HashSet<>();
        Set<SimpleDTO> genres = new HashSet<>();
        Set<SimpleDTO> themes = new HashSet<>();
        Set<SimpleDTO> perspectives = new HashSet<>();
        Set<SimpleDTO> keywords = new HashSet<>();
        Set<AgeRatingDTO> ageRatings = new HashSet<>();

        // Single traversal
        for (GamesDTO dto : gamesToPersist) {
            if (dto.platforms() != null) platforms.addAll(dto.platforms());
            if (dto.franchises() != null) franchises.addAll(dto.franchises());
            if (dto.gameType() != null) gameTypes.add(dto.gameType());
            if (dto.gameModes() != null) gameModes.addAll(dto.gameModes());
            if (dto.gameStatus() != null) gameStatuses.add(dto.gameStatus());
            if (dto.genres() != null) genres.addAll(dto.genres());
            if (dto.themes() != null) themes.addAll(dto.themes());
            if (dto.perspectives() != null) perspectives.addAll(dto.perspectives());
            if (dto.keywords() != null) keywords.addAll(dto.keywords());
            if (dto.ageRatings() != null) ageRatings.addAll(dto.ageRatings());
        }


        return PersistenceUtils.persistAllBatch(entityManager, List.of(
                new PersistenceUtils.Batch<>(franchises, Franchise.class, SimpleDTO::id, Franchise::parseDTO),
                new PersistenceUtils.Batch<>(gameModes, Gamemode.class, SimpleDTO::id, Gamemode::parseDTO),
                new PersistenceUtils.Batch<>(genres, Genre.class, SimpleDTO::id, Genre::parseDTO),
                new PersistenceUtils.Batch<>(themes, Theme.class, SimpleDTO::id, Theme::parseDTO),
                new PersistenceUtils.Batch<>(perspectives, Perspective.class, SimpleDTO::id, Perspective::parseDTO),
                new PersistenceUtils.Batch<>(keywords, Keyword.class, SimpleDTO::id, Keyword::parseDTO),
                new PersistenceUtils.Batch<>(gameStatuses, Gamestatus.class, GameStatusDTO::id, Gamestatus::parseDTO),
                new PersistenceUtils.Batch<>(platforms, Platform.class, PlatformDTO::id, Platform::parseDTO),
                new PersistenceUtils.Batch<>(gameTypes, Gametype.class, GameTypesDTO::id, Gametype::parseDTO),
                new PersistenceUtils.Batch<>(ageRatings, AgeRating.class, AgeRatingDTO::id, AgeRating::parseDTO)
        ));
    }

    private void loadMediaAssets(List<Game> games) {
      Log.info("Starting loading games assets...");
      int total = games.size();

      ProgressBar progressBar = ProgressBar.builder().setTaskName("Loading assets...").setInitialMax(total).setStyle(ProgressBarStyle.ASCII).build();
      ExecutorService executor = Executors.newFixedThreadPool(10);
      CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
      for (Game game : games) {
        completionService.submit(() -> {
          var assets = steamGridService.GetGameAsset(
            game.id,
            game.name,
            game.steamAppId != null ? Integer.valueOf(game.steamAppId) : null
          );
          if (game.cover == null) {
            game.cover = assets.cover();
          }
          game.hero = assets.hero();
          game.logo = assets.logo();
          return null;
        });
      }
      try {
        for (int i = 0; i < total; i++) {
          completionService.take(); // blocks until next finishes
          progressBar.step();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        Log.errorf("Load assets interrupted: %e", e);
      } finally {
        progressBar.close();
        executor.shutdown();
      }
      Log.info("Game assets loaded");
    }

}
