package gg.harvester.sgdb;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class SteamGridGameResponse {

    public boolean success;
    public SteamGridGame data;
}
