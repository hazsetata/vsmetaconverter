package com.hazse.vsmeta.vsmetaconverter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaContainer {
    @JsonProperty("com.synology.TheMovieDb")
    MetaReferenceHolder movieReferenceHolder;

    @JsonProperty("com.synology.TheTVDB")
    MetaReferenceHolder tvReferenceHolder;
}
