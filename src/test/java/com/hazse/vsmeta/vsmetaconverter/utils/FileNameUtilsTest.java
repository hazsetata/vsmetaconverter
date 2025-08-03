package com.hazse.vsmeta.vsmetaconverter.utils;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileNameUtilsTest {
    public static Stream<Arguments> positiveNfoFileNameArguments() {
        return Stream.of(
                Arguments.of(new File("Movie.vsmeta"), "Movie.nfo"),
                Arguments.of(new File("MV.avi.vsmeta"), "MV.nfo"),
                Arguments.of(new File("Rio Bravo cd1.avi.vsmeta"), "Rio Bravo cd1.nfo"),
                Arguments.of(new File("T.J. Hooker.S01E03.DVDRip.XviD.HUN.ENG-Baggio1.avi.vsmeta"), "T.J. Hooker.S01E03.DVDRip.XviD.HUN.ENG-Baggio1.nfo"),
                Arguments.of(new File("/any/path/Movie.mp4.vsmeta"), "Movie.nfo")
        );
    }

    @ParameterizedTest
    @MethodSource("positiveNfoFileNameArguments")
    void testNfoFileNameFor(File file, String expectedNfoFileName) {
        String result = FileNameUtils.getNfoFileNameFor(file);
        assertThat(result).isEqualTo(expectedNfoFileName);
    }

    public static Stream<Arguments> negativeNfoFileNameArguments() {
        return Stream.of(
                Arguments.of(new File("Movie.avi"), IllegalArgumentException.class, "File must be a vsmeta file."),
                Arguments.of(null, IllegalArgumentException.class, "File must not be null.")
        );
    }

    @ParameterizedTest
    @MethodSource("negativeNfoFileNameArguments")
    void throwsWhenFileDoesNotEndWithVsmeta(File file, Class<? extends Throwable> expectedException, String expectedMessage) {
        assertThatThrownBy(() -> FileNameUtils.getNfoFileNameFor(file))
                .isInstanceOf(expectedException)
                .hasMessageContaining(expectedMessage);
    }

    public static Stream<Arguments> isVsmetaFilePositiveArgs() {
        return Stream.of(
                Arguments.of("Movie.vsmeta"),
                Arguments.of("MV.avi.vsmeta")
        );
    }

    @ParameterizedTest
    @MethodSource("isVsmetaFilePositiveArgs")
    void testIsVsmetaFile_Positive(String fileName, @TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve(fileName);
        Files.createDirectories(filePath.getParent());
        Files.createFile(filePath);
        assertThat(FileNameUtils.isVsmetaFile(filePath.toFile())).isTrue();
    }

    public static Stream<Arguments> isVsmetaFileNegativeArgs() {
        return Stream.of(
            // existing non-vsmeta file
            Arguments.of("Movie.avi", false),
            // existing directory
            Arguments.of("someDir", true),
            // directory ending with .vsmeta
            Arguments.of("folder.vsmeta", true),
            // uppercase extension should not match (case-sensitive)
            Arguments.of("UPPER.VSMETA", false)
        );
    }

    @ParameterizedTest
    @MethodSource("isVsmetaFileNegativeArgs")
    void testIsVsmetaFile_Negative(String name, boolean asDirectory, @TempDir Path tempDir) throws Exception {
        Path path = tempDir.resolve(name);
        if (asDirectory) {
            Files.createDirectory(path);
        } else {
            Files.createFile(path);
        }
        assertThat(FileNameUtils.isVsmetaFile(path.toFile())).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void testIsVsmetaFile_ThrowsOnNull(File input) {
        assertThatThrownBy(() -> FileNameUtils.isVsmetaFile(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File must not be null.");
    }

    public static Stream<Arguments> isAllowedFilePositiveArgs() {
        return Stream.of(
                Arguments.of("dir", true), // directory allowed
                Arguments.of("video.vsmeta", false) // vsmeta file allowed
        );
    }

    @ParameterizedTest
    @MethodSource("isAllowedFilePositiveArgs")
    void testIsAllowedFile_Positive(String name, boolean asDirectory, @TempDir Path tempDir) throws Exception {
        Path path = tempDir.resolve(name);
        if (asDirectory) {
            Files.createDirectory(path);
        } else {
            Files.createFile(path);
        }
        assertThat(FileNameUtils.isAllowedFile(path.toFile())).isTrue();
    }

    public static Stream<Arguments> isAllowedFileNegativeArgs() {
        return Stream.of(
                Arguments.of("not-allowed.txt"),
                Arguments.of("movie.avi"),
                Arguments.of("UPPER.VSMETA") // not vsmeta due to case-sensitivity
        );
    }

    @ParameterizedTest
    @MethodSource("isAllowedFileNegativeArgs")
    void testIsAllowedFile_Negative(String fileName, @TempDir Path tempDir) throws Exception {
        Path filePath = tempDir.resolve(fileName);
        Files.createFile(filePath);
        assertThat(FileNameUtils.isAllowedFile(filePath.toFile())).isFalse();
    }

    @ParameterizedTest
    @NullSource
    void testIsAllowedFile_ThrowsOnNull(File input) {
        assertThatThrownBy(() -> FileNameUtils.isAllowedFile(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File must not be null.");
    }
}
