package gg.harvester;

import gg.harvester.igdb.game.GameService;
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

    public Main(
            GameService gameService,
            SqliteMaintenanceService maintenanceService
            ) {
        this.gameService = gameService;
        this.maintenanceService = maintenanceService;
    }

    @Override
    public void run() {
        gameService.importGamesByPlatform("PS5");
//        maintenanceService.vacuum();
        Quarkus.waitForExit();
    }

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory).execute(args);
    }
}
