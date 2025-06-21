package com.hazse.vsmeta.vsmetaconverter;

import com.hazse.vsmeta.vsmetaconverter.command.VsmetaConverterCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@RequiredArgsConstructor
public class VsmetaConverterApplication implements CommandLineRunner, ExitCodeGenerator {
    private final CommandLine.IFactory factory;
    private final VsmetaConverterCommand converterCommand;
    private int exitCode;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(VsmetaConverterApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(converterCommand,  factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
