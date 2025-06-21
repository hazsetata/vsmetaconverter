package com.hazse.vsmeta.vsmetaconverter.parser;

import com.hazse.vsmeta.vsmetaconverter.parser.meta.VSInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import static com.hazse.vsmeta.vsmetaconverter.parser.Tags.*;

@Component
@Slf4j
public class VsmetaParser {
    private final SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    public VSInfo readVsMeta(File that) {
        return parse(new MemorySyncStream(fileReadAll(that)), new VSInfo());
    }

    public VSInfo readVsMeta(InputStream that) {
        try {
            return parse(new MemorySyncStream(IOUtils.toByteArray(that)), new VSInfo());
        }
        catch (IOException e) {
            throw new VsmetaParserException("Error parsing input stream", e);
        }
    }

    private VSInfo parse(SyncStream s, VSInfo info) {
        int magic = s.readU8();
        int type = s.readU8();

        if (magic != 0x08) throw new VsmetaParserException("Not a vsmeta file");
        switch (type) {
            case 0x01: log.info("    vsmeta file represents movie information"); info.movie = true; break;
            case 0x02: log.info("    vsmeta file represents tv-episode information"); info.movie = false; break;
            default: throw new VsmetaParserException("    Unsupported vsmeta file type: " + type);
        }

        while (!s.eof()) {
            long pos = s.position();
            int kind = s.readU_VL_Int();
            switch (kind) {
                case TAG_SHOW_TITLE:
                    info.showTitle = s.readStringVL();
                    break;
                case TAG_SHOW_TITLE2:
                    info.showTitle2 = s.readStringVL();
                    break;
                case TAG_EPISODE_TITLE:
                    info.episodeTitle = s.readStringVL();
                    break;
                case TAG_YEAR:
                    info.year = s.readU_VL_Int();
                    break;
                case TAG_EPISODE_RELEASE_DATE:
                    String releaseDate = s.readStringVL();

                    try {
                        info.episodeReleaseDate = FORMAT_DATE.parse(releaseDate);
                    }
                    catch (ParseException e) {
                        log.warn("Couldn't parse {} into a release date", releaseDate);
                    }
                    break;
                case TAG_EPISODE_LOCKED:
                    info.episodeLocked = s.readU_VL_Int() != 0;
                    break;
                case TAG_CHAPTER_SUMMARY:
                    info.chapterSummary = s.readStringVL();
                    break;
                case TAG_EPISODE_META_JSON:
                    info.tagEpisodeMetaJson = s.readStringVL();
                    break;
                case TAG_GROUP1:
                    parseGroup1(openSync(s.readBytesVL()), info);
                    break;
                case TAG_CLASSIFICATION:
                    info.classification = s.readStringVL();
                    break;
                case TAG_RATING:
                    final int it = s.readU_VL_Int();
                    if (it < 0) {
                        info.rating = null;
                    } else {
                        info.rating = ((double) it) / 10;
                    }
                    break;
                case TAG_EPISODE_THUMB_DATA:
                    info.images.episodeImage = fromBase64IgnoreSpaces(s.readStringVL());
                    break;
                case TAG_EPISODE_THUMB_MD5:
                    assert (hex(md5(info.images.episodeImage)).equals(s.readStringVL()));
                    break;
                case TAG_GROUP2: {
                    int dataSize = s.readU_VL_Int();
                    long pos2 = s.position();
                    byte[] data = s.readBytes(dataSize);
                    parseGroup2(openSync(data), info, (int) pos2);
                    break;
                }
                case TAG_GROUP3: {
                    int dataSize = s.readU_VL_Int();
                    long pos2 = s.position();
                    byte[] data = s.readBytes(dataSize);
                    parseGroup3(openSync(data), info, (int) pos2);
                    break;
                }
                default:
                    throw new VsmetaParserException("[MAIN] Unexpected kind=" + kind + " at position=" + pos);
            }
        }

        return info;
    }

    private SyncStream parseGroup1(SyncStream s, VSInfo info) {
        while (!s.eof()) {
            long pos = s.position();
            int kind = s.readU_VL_Int();

            switch (kind) {
                case TAG1_CAST:
                    info.list.cast.add(s.readStringVL());
                    break;
                case TAG1_DIRECTOR:
                    info.list.director.add(s.readStringVL());
                    break;
                case TAG1_GENRE:
                    info.list.genre.add(s.readStringVL());
                    break;
                case TAG1_WRITER:
                    info.list.writer.add(s.readStringVL());
                    break;
                default:
                    throw new VsmetaParserException("[GROUP1] Unexpected kind=" + kind + " at position=" + pos);
            }
        }

        return s;
    }

    private SyncStream parseGroup2(SyncStream s, VSInfo info, int start) {
        while (!s.eof()) {
            long pos = s.position();
            int kind = s.readU_VL_Int();

            switch (kind) {
                case TAG2_SEASON:
                    info.season = s.readU_VL_Int();
                    break;
                case TAG2_EPISODE:
                    info.episode = s.readU_VL_Int();
                    break;
                case TAG2_TV_SHOW_YEAR:
                    info.tvshowYear = s.readU_VL_Int();
                    break;
                case TAG2_RELEASE_DATE_TV_SHOW:
                    String releaseDate = s.readStringVL();

                    try {
                        info.tvshowReleaseDate = FORMAT_DATE.parse(releaseDate);
                    } catch (ParseException e) {
                        log.warn("Couldn't parse {} into a tv-show release date", releaseDate);
                    }

                    break;
                case TAG2_LOCKED:
                    info.tvshowLocked = s.readU_VL_Int() != 0;
                    break;
                case TAG2_TVSHOW_SUMMARY:
                    info.tvshowSummary = s.readStringVL();
                    break;
                case TAG2_POSTER_DATA:
                    info.images.tvshowPoster = fromBase64IgnoreSpaces(s.readStringVL());
                    break;
                case TAG2_POSTER_MD5:
                    assert (s.readStringVL().equals(hex(md5(info.images.tvshowPoster))));
                    break;
                case TAG2_TVSHOW_META_JSON:
                    info.tagTvshowMetaJson = s.readStringVL();
                    break;
                case TAG2_GROUP3: { // GROUP3
                    int dataSize = s.readU_VL_Int();
                    int start2 = (int) s.position();
                    byte[] data = s.readBytes(dataSize);

                    parseGroup3(openSync(data), info, start2 + start);
                    break;
                }
                default:
                    throw new VsmetaParserException("[GROUP2] Unexpected kind=" + kind + " at position=" + (start + pos));
            }
        }
        return s;
    }

    private SyncStream parseGroup3(SyncStream s, VSInfo info, int start) {
        while (!s.eof()) {
            long pos = s.position();
            int kind = s.readU_VL_Int();

            switch (kind) {
                case TAG3_BACKDROP_DATA:
                    info.images.tvshowBackdrop = fromBase64IgnoreSpaces(s.readStringVL());
                    break;
                case TAG3_BACKDROP_MD5:
                    assert (s.readStringVL().equals(hex(md5(info.images.tvshowBackdrop))));
                    break;
                case TAG3_TIMESTAMP:
                    info.timestamp = new Date(s.readU_VL_Long() * 1000L);
                    break;
                default:
                    throw new VsmetaParserException("[GROUP3] Unexpected kind=" + kind + " at position=" + (start + pos));
            }
        }

        return s;
    }

    private byte[] fileReadAll(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return IOUtils.toByteArray(fis);
        }
        catch (Exception e) {
            throw new VsmetaParserException("File reading failed for: " + file.getAbsolutePath(), e);
        }
    }

    private SyncStream openSync(byte[] data) {
        return new MemorySyncStream(data);
    }

    private byte[] fromBase64IgnoreSpaces(String str) {
        return Base64.getDecoder().decode(str.replaceAll("\\s+", ""));
    }

    private byte[] md5(byte[] data) {
        try {
            if (data == null) data = new byte[0];
            return MessageDigest.getInstance("MD5").digest(data);
        }
        catch (NoSuchAlgorithmException e) {
            throw new VsmetaParserException("Digest algorithm MD5 missing");
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private String hex(byte[] data) {
        final String chars = "0123456789abcdef";
        char[] out = new char[data.length * 2];
        for (int n = 0; n < data.length; n++) {
            int v = data[n];
            out[n * 2 + 0] = chars.charAt((v >> 4) & 0xF);
            out[n * 2 + 1] = chars.charAt((v >> 0) & 0xF);
        }
        return new String(out);
    }
}
