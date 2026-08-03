package gg.harvester.sgdb;

import gg.harvester.NotFoundMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="sgdb")
@RegisterProvider(SGDBAuthFilter.class)
@RegisterProvider(NotFoundMapper.class)
@Path("/")
public interface SteamGridDBClient {

  @GET
  @Path("/search/autocomplete/{query}")
  @Produces(MediaType.APPLICATION_JSON)
  SteamGridGameListResponse search(@PathParam("query") String query);

  @GET
  @Path("/games/steam/{steamAppId}")
  @Produces(MediaType.APPLICATION_JSON)
  SteamGridGameResponse getBySteamId(@PathParam("steamAppId") String steamAppId);

  @GET
  @Path("/grids/game/{gameId}")
  @Produces(MediaType.APPLICATION_JSON)
  SteamGridResourceListResponse covers (
    @PathParam("gameId") int gameId,
    @QueryParam("types") @DefaultValue("static") String types,
    @QueryParam("nsfw") @DefaultValue("false") boolean nsfw
  );

  @GET
  @Path("/heroes/game/{gameId}")
  @Produces(MediaType.APPLICATION_JSON)
  SteamGridResourceListResponse heroes (
    @PathParam("gameId") int gameId,
    @QueryParam("types") @DefaultValue("static") String types,
    @QueryParam("nsfw") @DefaultValue("false") boolean nsfw
  );

  @GET
  @Path("/logos/game/{gameId}")
  @Produces(MediaType.APPLICATION_JSON)
  SteamGridResourceListResponse logos (
    @PathParam("gameId") int gameId,
    @QueryParam("types") @DefaultValue("static") String types,
    @QueryParam("nsfw") @DefaultValue("false") boolean nsfw
  );

 }
