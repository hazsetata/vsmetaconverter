package com.hazse.vsmeta.vsmetaconverter.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@Component
@CommandLine.Command(name = "vsmeta")
@Slf4j
public class VsmetaConverterCommand implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        log.info("Vsmeta Converter Command");

        return 0;
    }
}
