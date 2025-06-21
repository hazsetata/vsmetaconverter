package com.hazse.vsmeta.vsmetaconverter.parser;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Tags {
    static final int TAG_SHOW_TITLE = 0x12;
    static final int TAG_SHOW_TITLE2 = 0x1A;
    static final int TAG_EPISODE_TITLE = 0x22;
    static final int TAG_YEAR = 0x28;
    static final int TAG_EPISODE_RELEASE_DATE = 0x32;
    static final int TAG_EPISODE_LOCKED = 0x38;
    static final int TAG_CHAPTER_SUMMARY = 0x42;
    static final int TAG_EPISODE_META_JSON = 0x4A;
    static final int TAG_GROUP1 = 0x52;
    static final int TAG_CLASSIFICATION = 0x5A;
    static final int TAG_RATING = 0x60;

    static final int TAG_EPISODE_THUMB_DATA = 0x8a;
    static final int TAG_EPISODE_THUMB_MD5 = 0x92;

    static final int TAG_GROUP2 = 0x9a;

    static final int TAG1_CAST = 0x0A;
    static final int TAG1_DIRECTOR = 0x12;
    static final int TAG1_GENRE = 0x1A;
    static final int TAG1_WRITER = 0x22;

    static final int TAG2_SEASON = 0x08;
    static final int TAG2_EPISODE = 0x10;
    static final int TAG2_TV_SHOW_YEAR = 0x18;
    static final int TAG2_RELEASE_DATE_TV_SHOW = 0x22;
    static final int TAG2_LOCKED = 0x28;
    static final int TAG2_TVSHOW_SUMMARY = 0x32;
    static final int TAG2_POSTER_DATA = 0x3A;
    static final int TAG2_POSTER_MD5 = 0x42;
    static final int TAG2_TVSHOW_META_JSON = 0x4A;
    static final int TAG2_GROUP3 = 0x52;

    static final int TAG3_BACKDROP_DATA = 0x0a;
    static final int TAG3_BACKDROP_MD5 = 0x12;
    static final int TAG3_TIMESTAMP = 0x18;
}
