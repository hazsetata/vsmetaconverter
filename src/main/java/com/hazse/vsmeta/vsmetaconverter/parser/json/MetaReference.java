package com.hazse.vsmeta.vsmetaconverter.parser.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaReference {
    @JsonProperty("imdb")
    private String imdbId;

    @JsonProperty("themoviedb")
    private String movieDbId;

    @JsonProperty("thetvdb")
    private String tvDbId;
}
