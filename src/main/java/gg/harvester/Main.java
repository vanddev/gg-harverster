package gg.harvester;

import gg.harvester.igdb.game.GameService;
import gg.harvester.sgdb.SteamGridService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;

@QuarkusMain
@Command(mixinStandardHelpOptions = true)
public class Main implements Runnable, QuarkusApplication {

    private final SqliteMaintenanceService maintenanceService;
    @CommandLine.Option(names = {"-p", "--platforms"}, description = "Platform short names to import")
    List<String> platforms;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "Verbose output")
    boolean verbose;

    @Inject
    CommandLine.IFactory factory;

    private final GameService gameService;
    private final SteamGridService steamGridService;

    public Main(
            GameService gameService,
            SqliteMaintenanceService maintenanceService,
            SteamGridService steamGridService
            ) {
        this.gameService = gameService;
        this.maintenanceService = maintenanceService;
        this.steamGridService = steamGridService;
    }

    @Override
    public void run() {

        gameService.importGamesByPlatform("PS5");
//        maintenanceService.vacuum();
//        System.out.println(steamGridService.GetGameAsset("Mario Kart 8", null).toString());
        Quarkus.waitForExit();
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }
}
