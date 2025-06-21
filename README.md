# Introduction

Converts [Synology VideoStation](https://kb.synology.com/en-ph/DSM/help/VideoStation/VideoStation_desc?version=7)'s 
`vsmeta` files to simple `nfo` files to be consumed by [Emby](https://emby.media/). This can help with the migration
from VideoStation as it is discontinued since DSM 7.2.2.

# Acknowledgements
 * Uses the public-domain vsmeta parser classes from 'soywiz': https://gist.github.com/soywiz/2c10feb1231e70aca19a58aca9d6c16a
 * Inspired by: https://github.com/TomMeHo/vsMetaFileEncoder

# Notes
 * TV-DB format: https://www.thetvdb.com/series/{series_id}/episodes/{episode_id}
 * https://www.thetvdb.com/series/77076/episodes/227220