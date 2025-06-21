package com.hazse.vsmeta.vsmetaconverter.parser;

import com.hazse.vsmeta.vsmetaconverter.parser.meta.VSInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class VsmetaParserTest {
    @Autowired
    private VsmetaParser vsmetaParser;

    public static Stream<Arguments> vsmetaFiles() {
        return Stream.of(
                Arguments.of("__files/MV.avi.vsmeta", true),
                Arguments.of("__files/Rio Bravo cd1.avi.vsmeta", true),
                Arguments.of("__files/T.J. Hooker.S01E03.DVDRip.XviD.HUN.ENG-Baggio1.avi.vsmeta", false)
        );
    }

    @ParameterizedTest
    @MethodSource("vsmetaFiles")
    @DisplayName("Test readVsMeta with various .vsmeta files")
    void readVsMetaFromInputStream(String filePath, boolean movie) throws IOException {
        ClassPathResource resource = new ClassPathResource(filePath);

        VSInfo vsInfo;
        try (InputStream inputStream = resource.getInputStream()) {
            vsInfo = vsmetaParser.readVsMeta(inputStream);
            
            assertThat(vsInfo).isNotNull();

            if (movie) {
                assertThat(vsInfo.movie).isTrue();
            }
            else {
                assertThat(vsInfo.movie).isFalse();
                assertThat(vsInfo.season).isPositive();
                assertThat(vsInfo.episode).isPositive();
            }
        }
    }
    
    @Test
    @DisplayName("Test readVsMeta throws VsmetaParserException when InputStream throws IOException")
    void readVsMetaFromInputStreamShouldThrowVsmetaParserExceptionWhenIOExceptionOccurs() {
        InputStream mockInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test exception");
            }
        };
        
        assertThatThrownBy(() -> vsmetaParser.readVsMeta(mockInputStream))
                .isInstanceOf(VsmetaParserException.class)
                .hasMessageContaining("Error parsing input stream");
    }
}