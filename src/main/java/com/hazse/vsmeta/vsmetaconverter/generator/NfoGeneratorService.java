package com.hazse.vsmeta.vsmetaconverter.generator;

import com.hazse.vsmeta.vsmetaconverter.parser.VsmetaJsonParser;
import com.hazse.vsmeta.vsmetaconverter.parser.json.MetaContainer;
import com.hazse.vsmeta.vsmetaconverter.parser.json.MetaReference;
import com.hazse.vsmeta.vsmetaconverter.parser.meta.VSInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NfoGeneratorService {
    private final VsmetaJsonParser vsmetaJsonParser;

    public String generateNfo(VSInfo vsInfo) {
        if (vsInfo.movie) {
            return generateMovieNfo(vsInfo);
        }
        else if ((vsInfo.season > 0) && (vsInfo.episode > 0)) {
            return generateTvEpisodeInfo(vsInfo);
        }

        log.warn("Unable to generate NFO content.");
        return null;
    }

    private String generateTvEpisodeInfo(VSInfo vsInfo) {
        try {
            StringBuilderWriter retValue = new StringBuilderWriter();
            PrintWriter outputWriter = new PrintWriter(retValue);

            MetaContainer episodeContainer = vsmetaJsonParser.parseJson(vsInfo.tagEpisodeMetaJson);
            MetaContainer showContainer = vsmetaJsonParser.parseJson(vsInfo.tagTvshowMetaJson);

            if (StringUtils.hasText(vsInfo.showTitle)) {
                outputWriter.println(
                        String.format("TV Show: %s - %s (S%02dE%02d - %s)",
                                vsInfo.showTitle,
                                vsInfo.episodeTitle,
                                vsInfo.season,
                                vsInfo.episode,
                                vsInfo.year
                        )
                );
                outputWriter.println();
            }

            if (
                    (episodeContainer.getTvReferenceHolder() != null) &&
                    (episodeContainer.getTvReferenceHolder().getReference() != null) &&
                    (showContainer.getTvReferenceHolder() != null) &&
                    (showContainer.getTvReferenceHolder().getReference() != null)
            ) {
                MetaReference episodeReference = episodeContainer.getTvReferenceHolder().getReference();
                MetaReference showReference = showContainer.getTvReferenceHolder().getReference();

                if (StringUtils.hasText(episodeReference.getTvDbId()) && StringUtils.hasText(showReference.getTvDbId())) {
                    outputWriter.println(
                            String.format(
                                    "TvDB: https://www.thetvdb.com/series/%s/episodes/%s",
                                    showReference.getTvDbId(),
                                    episodeReference.getTvDbId()
                            )
                    );

                    return retValue.toString();
                }
            }

            log.warn("No episode / tv-show reference found in vsmeta file.");
            return null;
        }
        catch (Exception e) {
            log.error("Failed to generate NFO content for tv-show.", e);
            return null;
        }
    }

    private String generateMovieNfo(VSInfo vsInfo) {
        try {
            StringBuilderWriter retValue = new StringBuilderWriter();
            PrintWriter outputWriter = new PrintWriter(retValue);

            MetaContainer container = vsmetaJsonParser.parseJson(vsInfo.tagEpisodeMetaJson);

            if (StringUtils.hasText(vsInfo.showTitle)) {
                outputWriter.println(
                        String.format("Movie: %s (%s)",
                            vsInfo.showTitle,
                            vsInfo.year
                        )
                );
                outputWriter.println();
            }

            if ((container.getMovieReferenceHolder() != null) && (container.getMovieReferenceHolder().getReference() != null)) {
                MetaReference metaReference = container.getMovieReferenceHolder().getReference();
                boolean linkCreated = false;

                if (StringUtils.hasText(metaReference.getImdbId())) {
                    outputWriter.println(
                            String.format("IMDB: https://www.imdb.com/title/%s/", metaReference.getImdbId())
                    );
                    linkCreated = true;
                }
                if (StringUtils.hasText(metaReference.getMovieDbId())) {
                    outputWriter.println(
                            String.format("MovieDB: https://www.themoviedb.org/movie/%s", metaReference.getMovieDbId())
                    );
                    linkCreated = true;
                }

                if (linkCreated) {
                    return retValue.toString();
                }
            }

            log.warn("Not enough movie information found in vsmeta file.");
            return null;
        }
        catch (Exception e) {
            log.error("Failed to generate NFO content for movie.", e);
            return null;
        }
    }

}
