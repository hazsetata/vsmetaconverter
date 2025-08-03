# vsmetaconverter

## Introduction

Converts [Synology VideoStation](https://kb.synology.com/en-ph/DSM/help/VideoStation/VideoStation_desc?version=7)'s 
`.vsmeta` files to simple `.nfo` files to be consumed by [Emby](https://emby.media/). This can help with the migration
from VideoStation as it is discontinued since DSM 7.2.2. The created `.nfo` files contain a very small 
subset of the information in the `.vsmeta` file (IMDB, MovieDB and TvDB links), but this is enough for 
Emby to fetch all the up-to-date information from these sites. Emby can also generate a new `.nfo` file 
from the information it fetched.

## Usage

Once built (or if you download the pre-built binaries from [releases](https://github.com/hazsetata/vsmetaconverter/releases)), 
you can run the program with Java 21 like this:

```shell
java -jar vsmetaconverter-0.2.0.jar
```

This will print a help with all the possible options:

```terminaloutput
 __ __  _____ ___ ___    ___ ______   ____    __   ___   ____   __ __
|  |  |/ ___/|   |   |  /  _]      | /    |  /  ] /   \ |    \ |  |  |
|  |  (   \_ | _   _ | /  [_|      ||  o  | /  / |     ||  _  ||  |  |
|  |  |\__  ||  \_/  ||    _]_|  |_||     |/  /  |  O  ||  |  ||  |  |
|  :  |/  \ ||   |   ||   [_  |  |  |  _  /   \_ |     ||  |  ||  :  |
 \   / \    ||   |   ||     | |  |  |  |  \     ||     ||  |  | \   /
  \_/   \___||___|___||_____| |__|  |__|__|\____| \___/ |__|__|  \_/

 Synology VideoStation VSMeta file to NFO converter
 (v0.2.0 using SpringBoot 3.5.3)

2025-08-03T15:19:26.625+03:00  INFO 7433 --- [vsmetaconverter] [           main] c.h.v.v.VsmetaConverterApplicationTests  : Starting VsmetaConverterApplicationTests using Java 21.0.6 with PID 7433 (started by ...)
2025-08-03T15:19:26.626+03:00  INFO 7433 --- [vsmetaconverter] [           main] c.h.v.v.VsmetaConverterApplicationTests  : No active profile set, falling back to 1 default profile: "default"
2025-08-03T15:19:26.836+03:00  INFO 7433 --- [vsmetaconverter] [           main] c.h.v.v.VsmetaConverterApplicationTests  : Started VsmetaConverterApplicationTests in 0.354 seconds (process running for 0.76)
Missing required option: '--file=FILE'
Usage: vsmeta [-d] [--force-overwrite] -f=FILE
  -d, --dry-run           if enabled the generated NFO content is only
                            displayed but not saved
                            Default: false
  -f, --file=FILE         the input file or directory
      --force-overwrite   if enabled existing NFO files will be overwritten
                            Default: false
```

The `-f` / `--file` option is required: it can point to a single file or a directory. If it is a directory, all the 
`.vsmeta` files in the directory and all of its subdirectories will be processed.

```shell
java -jar vsmetaconverter-0.2.0.jar -f path/to/mymovie.mp4.vsmeta
```

```shell
java -jar vsmetaconverter-0.2.0.jar -f path/to/my-movies-directory/
```

## Acknowledgements
 * The vsmeta parser is a slightly modified version of the [public-domain classes from 'soywiz'](https://gist.github.com/soywiz/2c10feb1231e70aca19a58aca9d6c16a)
 * Inspired by: https://github.com/TomMeHo/vsMetaFileEncoder
