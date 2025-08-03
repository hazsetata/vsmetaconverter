package com.hazse.vsmeta.vsmetaconverter.command;

import com.hazse.vsmeta.vsmetaconverter.generator.NfoGeneratorService;
import com.hazse.vsmeta.vsmetaconverter.parser.VsmetaParser;
import com.hazse.vsmeta.vsmetaconverter.parser.meta.VSInfo;
import com.hazse.vsmeta.vsmetaconverter.utils.FileNameUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;

import static com.hazse.vsmeta.vsmetaconverter.utils.FileNameUtils.isAllowedFile;

@Component
@CommandLine.Command(name = "vsmeta")
@RequiredArgsConstructor
@Slf4j
public class VsmetaConverterCommand implements Callable<Integer> {
    public static final int EXECUTION_OK = 0;
    public static final int ERROR_INPUT_FILE_DOESNT_EXIST = 1;
    public static final int ERROR_INPUT_FILE_NOT_VSMETA = 2;

    private final VsmetaParser vsmetaParser;
    private final NfoGeneratorService nfoGeneratorService;

    @CommandLine.Option(
            names = { "-f", "--file" },
            paramLabel = "FILE",
            description = "the input file or directory",
            required = true
    )
    File inputFile;

    @CommandLine.Option(
            names = { "-d", "--dry-run" },
            description = "if enabled the generated NFO content is only displayed but not saved",
            defaultValue = "false",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    boolean dryRun = false;

    @CommandLine.Option(
            names = { "--force-overwrite" },
            description = "if enabled existing NFO files will be overwritten",
            defaultValue = "false",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    boolean forceOverwrite = false;

    @Override
    public Integer call() throws Exception {
        log.info("Vsmeta Converter Command");

        if (!inputFile.exists()) {
            log.error("Input file does not exist: {}", inputFile.getAbsolutePath());
            return ERROR_INPUT_FILE_DOESNT_EXIST;
        }
        else if (!isAllowedFile(inputFile)) {
            log.error("Input file needs to be a directory or a '.vsmeta' file.");
            return ERROR_INPUT_FILE_NOT_VSMETA;
        }
        else {
            long processedCount = 0;
            Queue<File> filesToProcess = new ArrayDeque<>();
            filesToProcess.offer(inputFile);

            while (!filesToProcess.isEmpty()) {
                File file = filesToProcess.poll();

                if (file.isDirectory()) {
                    log.info("Processing directory: {}", file.getAbsolutePath());
                    File[] directoryFiles = file.listFiles();
                    if (directoryFiles != null) {
                        for (File childFile : directoryFiles) {
                            if (isAllowedFile(childFile)) {
                                filesToProcess.offer(childFile);
                            }
                        }
                    }
                }
                else {
                    log.info("Processing file: {}", file.getAbsolutePath());
                    try {
                        VSInfo vsInfo = vsmetaParser.readVsMeta(file);
                        String nfoContent = nfoGeneratorService.generateNfo(vsInfo);

                        if (nfoContent != null) {
                            if (dryRun) {
                                log.info("NFO content:\n{}", nfoContent);
                            }
                            else {
                                saveNfoContent(nfoContent, file);
                            }
                        }
                        else {
                            log.warn("Unable to generate NFO file for file: {}", file.getAbsolutePath());
                        }
                    }
                    catch (Exception e) {
                        log.error("Failed to process file: {} - error: {}", file.getAbsolutePath(), e.getMessage());
                    }
                }

                processedCount++;
                log.info("Processed {} / remaining: {}", processedCount, filesToProcess.size());
            }
        }

        return EXECUTION_OK;
    }

    private void saveNfoContent(String nfoContent, File file) throws IOException {
        File nfoFile = new File(file.getParentFile(), FileNameUtils.getNfoFileNameFor(file));

        if (nfoFile.exists() && !forceOverwrite) {
            log.warn("NFO file already exists and force-overwrite is disabled: {}", nfoFile.getAbsolutePath());
            return;
        }

        try {
            Files.writeString(nfoFile.toPath(), nfoContent, StandardCharsets.UTF_8);
            log.info("Successfully saved NFO file: {}", nfoFile.getAbsolutePath());
        }
        catch (IOException e) {
            log.error("Failed to save NFO file: {} - error: {}", nfoFile.getAbsolutePath(), e.getMessage());
            throw e;
        }
    }
}
