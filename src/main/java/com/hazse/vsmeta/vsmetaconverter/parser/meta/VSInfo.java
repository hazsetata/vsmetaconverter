package com.hazse.vsmeta.vsmetaconverter.parser.meta;

import java.util.Date;

public class VSInfo {
    public String showTitle = "";
    public String showTitle2 = null;
    public String episodeTitle = "";
    public int year = 2019;
    public Date episodeReleaseDate = null;
    public Date tvshowReleaseDate = null;
    public int tvshowYear = 2019;
    public String tvshowSummary = "";
    public String chapterSummary = "";
    public String classification = "";
    public int season = 1;
    public int episode = 1;
    public Double rating = null;
    public ListInfo list = new ListInfo();
    public ImageInfo images = new ImageInfo();
    public String tagEpisodeMetaJson = "null";
    public String tagTvshowMetaJson = "null";
    public Date timestamp = new Date();
    public boolean episodeLocked = true;
    public boolean tvshowLocked = true;
    public boolean movie = true;
}
