package com.hazse.vsmeta.vsmetaconverter.parser;

import java.util.Arrays;

public class MemorySyncStream extends SyncStream {
    int position;
    int length;
    byte[] data;

    public MemorySyncStream(byte[] data) {
        this(data, 0);
    }

    public MemorySyncStream(byte[] data, int position) {
        this.data = data;
        this.position = position;
        this.length = data.length;
    }

    @Override
    long position() {
        return position;
    }

    @Override
    long length() {
        return length;
    }

    @Override
    public int readU8() {
        if (eof()) return -1;

        return this.data[position++];
    }

    public byte[] toByteArray() {
        return Arrays.copyOf(this.data, this.length);
    }
}
