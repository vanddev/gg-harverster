package gg.harvester.sgdb;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public class SteamGridResourceListResponse {

    public boolean success;
    public List<SteamGridResource> data;
}
