# YT-Mappings
YT-Mappings is a set of open, unencumbered YouTube app mappings, free for everyone to use under the Creative Commons Zero license.
The intention is to let everyone mod YouTube freely and openly, while also being able to innovate and process the mappings as they see fit.

To see the current version being targeted, check the branch name!


## Contributing
Please remember that copying and pasting mappings from alternate projects under more restrictive licenses is **completely forbidden** without explicit permission from the owners of said mappings to distribute the names under the CC0 license.
This includes using the names from those mappings for inspiration.
Discussing the naming approaches used in said projects is also not welcome - you have been warned.
However, it is a good idea to consult name changes with other people - use pull requests or our community spaces to ask questions!

Please have a look at the [naming conventions](/CONVENTIONS.md) before submitting mappings.


## Getting Started
1. Clone the repo
2. Download the [correct version]([./gradle.properties](https://github.com/NebelNidas/yt-mappings/blob/master/gradle.properties#L12)) of the YouTube APK, preferably from [APKMirror](https://www.apkmirror.com/apk/google-inc/youtube/) (`nodpi` is the default)
3. Place the APK at the root of this directory and rename it to `youtube.apk`
4. Run `./gradlew enigma` or `/gradlew jadx` to use and/or edit the mappings (see the tasks' descriptions below)
5. If you want to, commit and push your work to a fork and open a PR with your changes


## Gradle
YT-Mappings uses Gradle to provide a number of utility tasks for working with the mappings.
Please note, to run our build script **Java 17** is required!

### `enigma`
Download, setup and launch the latest version of [Enigma](https://github.com/FabricMC/Enigma) automatically configured to use the converted jar and the mappings.

Compared to launching Enigma externally, the gradle task adds a name guesser plugin that automatically maps enums and a few constant field names.

### `jadx`
Download, setup and launch the latest version of [JADX](https://github.com/skylot/jadx) automatically configured to use provided APK and the mappings.

### `buildTinyMappingFiles`
Build Tiny and Tiny v2 mapping files between official (obfuscated) names and our renames ("named").

### `buildNamedJar`
Builds a deobfuscated jar with YT-Mappings applied, called `youtube-<yt-version>-named.jar`.

### `decompileWithCfr`
Decompile the mapped source code with [CFR](https://github.com/leibnitz27/cfr) (quite slow, but has the best quality).

### `decompileWithQuiltflower`
Decompile the mapped source code with [QuiltFlower](https://github.com/QuiltMC/quiltflower) (faster, but produces more errors).

### `matcher`
Run [Matcher](https://github.com/NebelNidas/Matcher) with the provided `youtube.apk` and `youtube-new.apk` as inputs, so you can update the mappings to a newer YT version.
