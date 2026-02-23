package gg.harvester;

import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@ApplicationScoped
public class SqliteMaintenanceService {

    private final DataSource dataSource;

    public SqliteMaintenanceService(DataSource ds) {
        this.dataSource = ds;
    }

    public void vacuum() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(true);
            stmt.execute("PRAGMA optimize");
            stmt.execute("PRAGMA wal_checkpoint(TRUNCATE)");
            stmt.execute("VACUUM");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to run VACUMM", e);
        }
    }

}
