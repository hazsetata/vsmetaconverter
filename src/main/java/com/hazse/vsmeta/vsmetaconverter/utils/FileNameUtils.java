package com.hazse.vsmeta.vsmetaconverter.utils;

import lombok.experimental.UtilityClass;

import java.io.File;

@UtilityClass
public class FileNameUtils {
    public static final String VSMETA_EXTENSION = ".vsmeta";
    public static final String NFO_EXTENSION = ".nfo";

    public static String getNfoFileNameFor(File file) {
        if (file != null) {
            String originalName = file.getName();
            if (originalName.endsWith(VSMETA_EXTENSION)) {
                String baseName = originalName.substring(0, originalName.lastIndexOf(FileNameUtils.VSMETA_EXTENSION));

                if (baseName.contains(".")) {
                    baseName = baseName.substring(0, baseName.lastIndexOf("."));
                }

                return baseName + NFO_EXTENSION;
            }
            else {
                throw new IllegalArgumentException("File must be a vsmeta file.");
            }
        }
        else {
            throw new IllegalArgumentException("File must not be null.");
        }
    }

    public static boolean isVsmetaFile(File childFile) {
        if (childFile != null) {
            return childFile.isFile() && childFile.getName().endsWith(VSMETA_EXTENSION);
        }
        else {
            throw new IllegalArgumentException("File must not be null.");
        }
    }

    public static boolean isAllowedFile(File childFile) {
        if (childFile != null) {
            return childFile.isDirectory() || isVsmetaFile(childFile);
        }
        else {
            throw new IllegalArgumentException("File must not be null.");
        }
    }
}
