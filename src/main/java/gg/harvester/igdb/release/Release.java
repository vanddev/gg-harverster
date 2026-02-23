package gg.harvester.igdb.release;

import gg.harvester.igdb.BaseEntity;
import gg.harvester.igdb.game.Game;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "release")
public class Release extends BaseEntity {

    public static List<String> fields = List.of("id", "date", "release_region.region", "status.name" ,"platform", "game");
    public static String DEFAULT_STATUS = "Full Release";
    public static String DEFAULT_REGION = "worldwide";


    public String status;

    @Column(name="datetime")
    public Integer date;

    public String region;

    @Column(name = "platform_id")
    public Integer platformId;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    public Game game;

    public static Release parseDTO(ReleasesDTO dto, Game game) {
        Release release = new Release();
        release.id = dto.id();
        release.date = dto.date();
        release.region = dto.region() != null ? dto.region().region() : DEFAULT_REGION;
        release.status = dto.status() != null ? dto.status().name() : DEFAULT_STATUS;
        release.platformId = dto.platform();
        release.game = game;
        return release;
    }
}
