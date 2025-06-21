package com.hazse.vsmeta.vsmetaconverter.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazse.vsmeta.vsmetaconverter.parser.json.MetaContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VsmetaJsonParser {
    private final ObjectMapper objectMapper;

    public MetaContainer parseJson(String json) {
        try {
            return objectMapper.readValue(json, MetaContainer.class);
        }
        catch (JsonProcessingException e) {
            throw new VsmetaParserException("Failed to parse metadata JSON", e);
        }
    }
}
