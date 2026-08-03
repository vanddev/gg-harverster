package gg.harvester.sgdb;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class SteamGridGameListResponse {

    public boolean success;
    public List<SteamGridGame> data;
}
