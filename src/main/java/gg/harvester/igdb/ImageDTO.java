package gg.harvester.igdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageDTO(@JsonProperty("image_id") String imageId)  {
}
