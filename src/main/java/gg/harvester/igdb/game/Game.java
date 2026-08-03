package gg.harvester.igdb.game;

import gg.harvester.igdb.BaseEntity;
import gg.harvester.igdb.agerating.AgeRating;
import gg.harvester.igdb.externalgame.ExternalGameDTO;
import gg.harvester.igdb.franchise.Franchise;
import gg.harvester.igdb.gamemode.Gamemode;
import gg.harvester.igdb.gamestatus.Gamestatus;
import gg.harvester.igdb.gametype.Gametype;
import gg.harvester.igdb.genre.Genre;
import gg.harvester.igdb.keyword.Keyword;
import gg.harvester.igdb.perspective.Perspective;
import gg.harvester.igdb.platform.Platform;
import gg.harvester.igdb.release.Release;
import gg.harvester.igdb.theme.Theme;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "game")
public class Game extends BaseEntity {

    private static final int STEAM_EXTERNAL_SOURCE_ID = 1;

    public static List<String> fields = List.of(
            "name",
            "url",
            "cover.image_id",
            "aggregated_rating",
            "aggregated_rating_count",
            "rating",
            "rating_count",
            "total_rating",
            "total_rating_count",
            "external_games.uid",
            "external_games.external_game_source"
    );

    public String name;

    @Column(name = "igdb_url")
    public String igdbUrl;

    @Transient
    public String steamAppId;

    public String cover;

    public String logo;

    public String hero;

    @Column(name = "first_release_date")
    public Integer firstRelease;

    @Column(name = "players_rating")
    public Double playersRating;

    @Column(name = "players_rating_count")
    public Integer playersRatingCount;

    @Column(name = "critics_rating")
    public Double criticRating;

    @Column(name = "critics_rating_count")
    public Integer criticRatingCount;

    @Column(name = "rating")
    public Double rating;

    @Column(name = "rating_count")
    public Integer ratingCount;

    @ManyToOne
    @JoinColumn(name = "gametype_id")
    Gametype gametype;

    @ManyToOne
    @JoinColumn(name = "gamestatus_id")
    Gamestatus gamestatus;

    @OneToMany(mappedBy = "game", cascade = CascadeType.PERSIST)
    private Set<Release> releases;

    @ManyToMany
    @JoinTable(
        name = "game_franchise",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "franchise_id")
    )
    Set<Franchise> franchises;

    @ManyToMany
    @JoinTable(
            name = "game_platform",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "platform_id")
    )
    Set<Platform> platforms;

    @ManyToMany
    @JoinTable(
            name = "game_genre",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    Set<Genre> genres;

    @ManyToMany
    @JoinTable(
            name = "game_theme",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    Set<Theme> themes;

    @ManyToMany
    @JoinTable(
            name = "game_gamemode",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "gamemode_id")
    )
    Set<Gamemode> gamemodes;

    @ManyToMany
    @JoinTable(
            name = "game_player_perspective",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_perspective_id")
    )
    Set<Perspective> perspectives;

    @ManyToMany
    @JoinTable(
            name = "game_age_rating",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "age_rating_id")
    )
    Set<AgeRating> ageRatings;

    @ManyToMany
    @JoinTable(
            name = "game_keyword",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    Set<Keyword> keywords;

    public void setReleases(Set<Release> releases) {
        this.releases = releases;

        var dates = releases.stream().map(r -> r.date).sorted().toList();

        this.firstRelease = dates.stream().findFirst().orElse(null);
    }

    public Set<Release> getReleases() {
        return this.releases;
    }

    private static String buildCoverURL(String coverId){
      return String.format("https://images.igdb.com/igdb/image/upload/t_cover_big/%s.jpg", coverId);
    }

    public static Game parseDTO(GamesDTO dto) {
        var game = new Game();
        game.id = dto.id();
        game.name = dto.name();
        game.igdbUrl = dto.url();
        game.cover = dto.cover() != null ? buildCoverURL(dto.cover().imageId()) : null;
        game.criticRating = dto.aggregatedRating();
        game.criticRatingCount = dto.aggregatedRatingCount();
        game.playersRating = dto.rating();
        game.playersRatingCount = dto.ratingCount();
        game.rating = dto.totalRating();
        game.ratingCount = dto.totalRatingCount();
        game.steamAppId = dto.externalGames() != null ? dto.externalGames().stream()
            .filter(e -> e.externalGameSource() == STEAM_EXTERNAL_SOURCE_ID)
            .map(ExternalGameDTO::uid)
            .findFirst()
            .orElse(null) : null;
        return game;
    }
}
