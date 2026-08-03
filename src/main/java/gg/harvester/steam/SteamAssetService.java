package gg.harvester.steam;

import gg.harvester.sgdb.AssetType;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.HttpURLConnection;
import java.net.URI;

@ApplicationScoped
public class SteamAssetService {

    public String Get(Integer steamAppId, AssetType assetType) {
        return switch (assetType) {
          case COVER -> getOfficialCover(steamAppId);
          case LOGO -> getOfficialLogo(steamAppId);
          case HERO -> getOfficialHero(steamAppId);
        };
    }

    private String getOfficialCover(Integer steamAppId) {
        return getOfficialAsset(steamAppId, "/library_600x900_2x.jpg");
    }

    private String getOfficialHero(Integer steamAppId) {
      return getOfficialAsset(steamAppId, "/library_hero.jpg");
    }

    private String getOfficialLogo(Integer steamAppId) {
      return getOfficialAsset(steamAppId, "/logo.png");
    }

    private String getOfficialAsset(Integer steamAppId, String filename) {
        if (steamAppId == null) return null;

        String url = "https://cdn.cloudflare.steamstatic.com/steam/apps/"
          + steamAppId + filename;

        if (exists(url)) {
          return url;
        }

        return null;
    }

    private boolean exists(String url)
    {
        try {
          HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
          conn.setRequestMethod("HEAD");
          conn.setConnectTimeout(2000);
          conn.connect();
          return conn.getResponseCode() == 200;
        } catch (Exception e) {
          return false;
        }
    }
}
