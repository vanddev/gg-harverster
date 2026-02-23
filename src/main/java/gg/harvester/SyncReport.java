package gg.harvester;

public record SyncReport(
        int processed,
        int added,
        int skipped
) {
}
