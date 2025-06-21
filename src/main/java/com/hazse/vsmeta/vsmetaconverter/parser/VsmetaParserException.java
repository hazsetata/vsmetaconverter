package com.hazse.vsmeta.vsmetaconverter.parser;

public class VsmetaParserException extends RuntimeException {
    public VsmetaParserException(String message) {
        super(message);
    }

    public VsmetaParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
