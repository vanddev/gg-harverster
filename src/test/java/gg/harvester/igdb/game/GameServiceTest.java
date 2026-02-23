package gg.harvester.igdb.game;

import gg.harvester.SyncReport;
import gg.harvester.igdb.BaseEntity;
import gg.harvester.igdb.CountDTO;
import gg.harvester.igdb.PersistenceUtils;
import gg.harvester.igdb.gamestatus.GameStatusDTO;
import gg.harvester.igdb.gamestatus.Gamestatus;
import gg.harvester.igdb.gamestatus.GamestatusService;
import gg.harvester.igdb.gametype.GameTypesDTO;
import gg.harvester.igdb.gametype.Gametype;
import gg.harvester.igdb.gametype.GametypeService;
import gg.harvester.igdb.platform.PlatformService;
import gg.harvester.igdb.release.ReleasesDTO;
import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

    @Mock
    private PlatformService platformService;

    @Mock
    private GameClient gameClient;

    @Mock
    private GametypeService gametypeService;

    @Mock
    private GamestatusService gamestatusService;

    @Mock
    private GameRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private GameService gameService;

    @Test
    void fetchGameCountByPlatform_returnsCountFromClient() {
        // Arrange
        Integer platformId = 1;
        CountDTO countDTO = new CountDTO(150);

        when(gameClient.fetchGamesCount(anyString())).thenReturn(countDTO);

        // Act
        Integer result = gameService.fetchGameCountByPlatform(platformId);

        // Assert
        assertEquals(150, result);
        verify(gameClient).fetchGamesCount(argThat(s -> s.contains("platforms=(1)")));
    }

    @Test
    void syncGames_whenAllGamesAlreadyPersisted_returnsCorrectSyncReport() {
        // Arrange
        GameTypesDTO gameType = new GameTypesDTO(0, "Main Game");
        ReleasesDTO release = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        GamesDTO game1 = createGameDTO(1, "Game 1", gameType, List.of(release), null, null);
        GamesDTO game2 = createGameDTO(2, "Game 2", gameType, List.of(release), null, null);
        List<GamesDTO> dtos = List.of(game1, game2);

        when(repository.findGameIds(List.of(1, 2))).thenReturn(List.of(1, 2));

        // Act
        SyncReport result = gameService.syncGames(dtos);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.processed());
        assertEquals(0, result.added());
        assertEquals(2, result.skipped());
        verify(repository, never()).persist(any(Game.class));
        verify(repository, never()).persist(any(Iterable.class));
    }

    @Test
    void syncGames_whenNoGamesPersisted_persistsAllGames() {
        // Arrange
        GameTypesDTO gameType = new GameTypesDTO(0, "Main Game");
        Gamestatus releasedStatus = new Gamestatus();
        releasedStatus.id = 1;
        releasedStatus.status = "Released";

        ReleasesDTO release = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        GamesDTO gameDTO = createGameDTO(1, "Game 1", gameType, List.of(release), null, null);
        List<GamesDTO> dtos = List.of(gameDTO);

        when(repository.findGameIds(List.of(1))).thenReturn(List.of()); // No persisted games

        try (MockedStatic<Panache> panacheMock = mockStatic(Panache.class)) {
            panacheMock.when(Panache::getEntityManager).thenReturn(entityManager);

            // Mock persistAllGameData behavior
            Map<String, Map<Integer, ? extends BaseEntity>> persistedData = new HashMap<>();
            persistedData.put("Gametype", Map.of(0, createMockGametype(0, "Main Game")));
            persistedData.put("Gamestatus", Map.of(1, releasedStatus));

            try (MockedStatic<PersistenceUtils> persistenceUtilsMock = mockStatic(PersistenceUtils.class)) {
                persistenceUtilsMock.when(() -> PersistenceUtils.persistAllBatch(any(EntityManager.class), anyList()))
                        .thenReturn(persistedData);

                when(gamestatusService.findReleasedStatus()).thenReturn(releasedStatus);

                // Act
                SyncReport result = gameService.syncGames(dtos);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.processed());
                assertEquals(1, result.added());
                assertEquals(0, result.skipped());
                verify(repository, atLeastOnce()).persist(any(Iterable.class));
            }
        }
    }

    @Test
    void syncGames_whenSomeGamesPersisted_persistsOnlyNewGames() {
        // Arrange
        GameTypesDTO gameType = new GameTypesDTO(0, "Main Game");
        Gamestatus releasedStatus = new Gamestatus();
        releasedStatus.id = 1;
        releasedStatus.status = "Released";

        ReleasesDTO release = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        GamesDTO persistedGame = createGameDTO(1, "Game 1", gameType, List.of(release), null, null);
        GamesDTO newGame = createGameDTO(2, "Game 2", gameType, List.of(release), null, null);
        List<GamesDTO> dtos = List.of(persistedGame, newGame);

        when(repository.findGameIds(List.of(1, 2))).thenReturn(List.of(1)); // Only game 1 is persisted

        try (MockedStatic<Panache> panacheMock = mockStatic(Panache.class)) {
            panacheMock.when(Panache::getEntityManager).thenReturn(entityManager);

            Map<String, Map<Integer, ? extends BaseEntity>> persistedData = new HashMap<>();
            persistedData.put("Gametype", Map.of(0, createMockGametype(0, "Main Game")));
            persistedData.put("Gamestatus", Map.of(1, releasedStatus));

            try (MockedStatic<PersistenceUtils> persistenceUtilsMock = mockStatic(PersistenceUtils.class)) {
                persistenceUtilsMock.when(() -> PersistenceUtils.persistAllBatch(any(EntityManager.class), anyList()))
                        .thenReturn(persistedData);

                when(gamestatusService.findReleasedStatus()).thenReturn(releasedStatus);

                // Act
                SyncReport result = gameService.syncGames(dtos);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.processed());
                assertEquals(1, result.added());
                assertEquals(1, result.skipped());
                verify(repository, atLeastOnce()).persist(any(Iterable.class));
            }
        }
    }

    @Test
    void syncGames_whenGameHasVersionParent_usesEditionType() {
        // Arrange
        Gametype editionType = new Gametype();
        editionType.id = 20;
        editionType.type = "Edition";

        Gamestatus releasedStatus = new Gamestatus();
        releasedStatus.id = 1;
        releasedStatus.status = "Released";

        ReleasesDTO release = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        GamesDTO gameDTO = createGameDTO(1, "Game 1", null, List.of(release), 999, null); // versionParent = 999
        List<GamesDTO> dtos = List.of(gameDTO);

        when(repository.findGameIds(List.of(1))).thenReturn(List.of());

        try (MockedStatic<Panache> panacheMock = mockStatic(Panache.class)) {
            panacheMock.when(Panache::getEntityManager).thenReturn(entityManager);

            Map<String, Map<Integer, ? extends BaseEntity>> persistedData = new HashMap<>();
            persistedData.put("Gamestatus", Map.of(1, releasedStatus));

            try (MockedStatic<PersistenceUtils> persistenceUtilsMock = mockStatic(PersistenceUtils.class)) {
                persistenceUtilsMock.when(() -> PersistenceUtils.persistAllBatch(any(EntityManager.class), anyList()))
                        .thenReturn(persistedData);

                when(gamestatusService.findReleasedStatus()).thenReturn(releasedStatus);
                when(gametypeService.findEditionType()).thenReturn(editionType);

                // Act
                gameService.syncGames(dtos);

                // Assert
                verify(gametypeService).findEditionType();
                verify(repository, atLeastOnce()).persist(any(Iterable.class));
            }
        }
    }

    @Test
    void syncGames_whenGameStatusIsNull_usesReleasedStatus() {
        // Arrange
        GameTypesDTO gameType = new GameTypesDTO(0, "Main Game");
        Gamestatus releasedStatus = new Gamestatus();
        releasedStatus.id = 1;
        releasedStatus.status = "Released";

        ReleasesDTO release = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        GamesDTO gameDTO = createGameDTO(1, "Game 1", gameType, List.of(release), null, null); // gameStatus = null
        List<GamesDTO> dtos = List.of(gameDTO);

        when(repository.findGameIds(List.of(1))).thenReturn(List.of());

        try (MockedStatic<Panache> panacheMock = mockStatic(Panache.class)) {
            panacheMock.when(Panache::getEntityManager).thenReturn(entityManager);

            Map<String, Map<Integer, ? extends BaseEntity>> persistedData = new HashMap<>();
            persistedData.put("Gametype", Map.of(0, createMockGametype(0, "Main Game")));

            try (MockedStatic<PersistenceUtils> persistenceUtilsMock = mockStatic(PersistenceUtils.class)) {
                persistenceUtilsMock.when(() -> PersistenceUtils.persistAllBatch(any(EntityManager.class), anyList()))
                        .thenReturn(persistedData);

                when(gamestatusService.findReleasedStatus()).thenReturn(releasedStatus);

                // Act
                gameService.syncGames(dtos);

                // Assert
                verify(gamestatusService).findReleasedStatus();
                verify(repository, atLeastOnce()).persist(any(Iterable.class));
            }
        }
    }

    @Test
    void syncGames_whenGameStatusIsProvided_usesProvidedStatus() {
        // Arrange
        GameTypesDTO gameType = new GameTypesDTO(0, "Main Game");
        GameStatusDTO gameStatusDTO = new GameStatusDTO(2, "Early Access");
        Gamestatus earlyAccessStatus = new Gamestatus();
        earlyAccessStatus.id = 2;
        earlyAccessStatus.status = "Early Access";

        ReleasesDTO release = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        GamesDTO gameDTO = createGameDTO(1, "Game 1", gameType, List.of(release), null, gameStatusDTO);
        List<GamesDTO> dtos = List.of(gameDTO);

        when(repository.findGameIds(List.of(1))).thenReturn(List.of());

        try (MockedStatic<Panache> panacheMock = mockStatic(Panache.class)) {
            panacheMock.when(Panache::getEntityManager).thenReturn(entityManager);

            Map<String, Map<Integer, ? extends BaseEntity>> persistedData = new HashMap<>();
            persistedData.put("Gametype", Map.of(0, createMockGametype(0, "Main Game")));
            persistedData.put("Gamestatus", Map.of(2, earlyAccessStatus));

            try (MockedStatic<PersistenceUtils> persistenceUtilsMock = mockStatic(PersistenceUtils.class)) {
                persistenceUtilsMock.when(() -> PersistenceUtils.persistAllBatch(any(EntityManager.class), anyList()))
                        .thenReturn(persistedData);

                // Act
                gameService.syncGames(dtos);

                // Assert
                verify(gamestatusService, never()).findReleasedStatus();
                verify(repository, atLeastOnce()).persist(any(Iterable.class));
            }
        }
    }

    @Test
    void syncGames_whenReleaseHasNullDate_filtersOutRelease() {
        // Arrange
        GameTypesDTO gameType = new GameTypesDTO(0, "Main Game");
        Gamestatus releasedStatus = new Gamestatus();
        releasedStatus.id = 1;
        releasedStatus.status = "Released";

        ReleasesDTO releaseWithDate = new ReleasesDTO(1, null, 1, 1, null, 1234567890);
        ReleasesDTO releaseWithoutDate = new ReleasesDTO(2, null, 1, 1, null, null);
        GamesDTO gameDTO = createGameDTO(1, "Game 1", gameType, 
                List.of(releaseWithDate, releaseWithoutDate), null, null);
        List<GamesDTO> dtos = List.of(gameDTO);

        when(repository.findGameIds(List.of(1))).thenReturn(List.of());

        try (MockedStatic<Panache> panacheMock = mockStatic(Panache.class)) {
            panacheMock.when(Panache::getEntityManager).thenReturn(entityManager);

            Map<String, Map<Integer, ? extends BaseEntity>> persistedData = new HashMap<>();
            persistedData.put("Gametype", Map.of(0, createMockGametype(0, "Main Game")));
            persistedData.put("Gamestatus", Map.of(1, releasedStatus));

            try (MockedStatic<PersistenceUtils> persistenceUtilsMock = mockStatic(PersistenceUtils.class)) {
                persistenceUtilsMock.when(() -> PersistenceUtils.persistAllBatch(any(EntityManager.class), anyList()))
                        .thenReturn(persistedData);

                when(gamestatusService.findReleasedStatus()).thenReturn(releasedStatus);

                // Act
                gameService.syncGames(dtos);

                // Assert
                verify(repository, atLeastOnce()).persist(any(Iterable.class));
            }
        }
    }

    // Helper method to create GamesDTO with all required parameters
    private GamesDTO createGameDTO(Integer id, String name, GameTypesDTO gameType, 
                                   List<ReleasesDTO> releases, Integer versionParent, 
                                   GameStatusDTO gameStatus) {
        return new GamesDTO(
                id,                              // id
                name,                            // name
                "http://example.com/game",       // url
                null,                            // cover
                null,                            // platforms
                null,                            // aggregatedRating
                null,                            // aggregatedRatingCount
                null,                            // rating
                null,                            // ratingCount
                null,                            // totalRating
                null,                            // totalRatingCount
                versionParent,                   // versionParent
                gameType,                        // gameType
                gameStatus,                      // gameStatus
                null,                            // gameModes
                releases,                        // releases
                null,                            // ageRatings
                null,                            // genres
                null,                            // perspectives
                null,                            // themes
                null,                            // keywords
                null,                            // franchises
                null                             // mainFranchise
        );
    }

    // Helper method to create mock Gametype
    private Gametype createMockGametype(Integer id, String type) {
        Gametype gametype = new Gametype();
        gametype.id = id;
        gametype.type = type;
        return gametype;
    }
}
