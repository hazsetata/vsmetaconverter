package com.hazse.vsmeta.vsmetaconverter.parser;

import com.hazse.vsmeta.vsmetaconverter.parser.json.MetaContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VsmetaJsonParserTest {
    @Autowired
    private VsmetaJsonParser vsmetaJsonParser;

    @Test
    void parseTvShowJson() {
        String tvShowJson = """
                {
                   "com.synology.Synovideodb" : {
                      "rating" : {
                         "synovideodb" : 6.10
                      },
                      "reference" : {
                         "synovideodb" : "im_1839975"
                      }
                   },
                   "com.synology.TheTVDB" : {
                      "reference" : {
                         "thetvdb" : "227220"
                      }
                   }
                }
                """;

        MetaContainer container = vsmetaJsonParser.parseJson(tvShowJson);

        assertThat(container).isNotNull();
        assertThat(container.getMovieReferenceHolder()).isNull();
        assertThat(container.getTvReferenceHolder()).isNotNull();
        assertThat(container.getTvReferenceHolder().getReference()).isNotNull();
        assertThat(container.getTvReferenceHolder().getReference().getImdbId()).isNull();
        assertThat(container.getTvReferenceHolder().getReference().getMovieDbId()).isNull();
        assertThat(container.getTvReferenceHolder().getReference().getTvDbId()).isEqualTo("227220");
    }

    public static Stream<Arguments> movieArguments() {
        return Stream.of(
                Arguments.of("""
                        {
                           "com.synology.TheMovieDb" : {
                              "backdrop" : [ "http://image.tmdb.org/t/p/original/3aAQZXUhJTGyuRX2i8A0NTLOYvz.jpg" ],
                              "poster" : [ "http://image.tmdb.org/t/p/w500/esfxVgZqueQ7y4srIFnU4aYJJFH.jpg" ],
                              "rating" : {
                                 "themoviedb" : 6.70
                              },
                              "reference" : {
                                 "imdb" : "tt0370919",
                                 "themoviedb" : 66926
                              }
                           }
                        }
                        """,
                        "tt0370919",
                        "66926"
                ),
                Arguments.of("""
                        {
                           "com.synology.Synovideodb" : {
                              "rating" : {
                                 "synovideodb" : 8.10
                              },
                              "reference" : {
                                 "synovideodb" : "im_3222020"
                              }
                           },
                           "com.synology.TheMovieDb" : {
                              "backdrop" : [ "http://image.tmdb.org/t/p/original/pGOkfCqtP09QbaFSRxrmF2BxZJj.jpg" ],
                              "poster" : [ "http://image.tmdb.org/t/p/w500/gyEfGVDe5puz4wgIq6073fn8pHc.jpg" ],
                              "rating" : {
                                 "themoviedb" : 7.40
                              },
                              "reference" : {
                                 "imdb" : "tt0053221",
                                 "themoviedb" : 301
                              }
                           }
                        }
                        """,
                        "tt0053221",
                        "301"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("movieArguments")
    void parseMovieJson(String json, String imdbId, String movieDbId) {
        MetaContainer container = vsmetaJsonParser.parseJson(json);

        assertThat(container).isNotNull();
        assertThat(container.getTvReferenceHolder()).isNull();
        assertThat(container.getMovieReferenceHolder()).isNotNull();
        assertThat(container.getMovieReferenceHolder().getReference()).isNotNull();
        assertThat(container.getMovieReferenceHolder().getReference().getImdbId()).isEqualTo(imdbId);
        assertThat(container.getMovieReferenceHolder().getReference().getMovieDbId()).isEqualTo(movieDbId);
        assertThat(container.getMovieReferenceHolder().getReference().getTvDbId()).isNull();
    }
}