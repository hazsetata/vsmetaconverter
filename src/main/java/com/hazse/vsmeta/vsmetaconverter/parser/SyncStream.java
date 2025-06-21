package com.hazse.vsmeta.vsmetaconverter.parser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class SyncStream {
    abstract long position();
    abstract long length();

    boolean eof() {
        return position() >= length();
    }

    boolean hasMore() {
        return !eof();
    }

    long available() {
        return length() - position();
    }

    public abstract int readU8();

    public void readExact(byte[] data, int offset, int length) {
        for (int n = 0; n < length; n++) {
            int v = readU8();
            data[offset + n] = (byte) v;
        }
    }

    public byte[] readBytes(int count) {
        byte[] out = new byte[Math.min(count, (int) available())];
        readExact(out, 0, out.length);

        return out;
    }
    public int readU_VL_Int() {
        return (int) readU_VL_Long();
    }

    public long readU_VL_Long() {
        long out = 0L;
        int offset = 0;
        int v;

        do {
            v = readU8();
            out = out | ((long) (v & 0x7F) << offset);
            offset += 7;
        } while ((v & 0x80) != 0);

        return out;
    }

    public byte[] readBytesVL() {
        byte[] bytes = new byte[readU_VL_Int()];
        readExact(bytes, 0, bytes.length);

        return bytes;
    }

    public String readStringVL() {
        return readStringVL(StandardCharsets.UTF_8);
    }

    public String readStringVL(Charset charset) {
        return new java.lang.String(readBytesVL(), charset);
    }
}
