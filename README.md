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
2. Download the correct version (see branch name) of the YouTube APK, preferably from [APKMirror](https://www.apkmirror.com/apk/google-inc/youtube/) (`nodpi` is the default)
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

### `matcher`
Run [Matcher](https://github.com/NebelNidas/Matcher) with the provided `youtube.apk` and `youtube-new.apk` as inputs, so you can update the mappings to a newer YT version.

As soon as Matcher is started and has analyzed its input files, select `Matching` → `Auto match all`. Depending on your Computer's hardware, this may take several hours, sometimes up to half a day. A modern multi-core CPU with ≥6 cores (12 threads) and ≥16GB RAM are recommended! After the process has finished, take care of the remaining red entries. You may have to double-check each class and method if any containing children are still unmatched. If you can't find a similar enough counterpart, perhaps the class/method/etc. has been removed from the newer APK. In this case, click the "unmatchable" button in the bottom pane.

Once you're confident with your results, go to `File` → `Save matches` and save the file into the `matches` folder. Please name it `<old-version> - <new-version>.match`; see the already existent match files.

To update the mappings, go to to `File` → `Save mappings (Enigma)`, select the `mappings` folder, give permission to overwrite the existing data and use the following configuration to export the updated files:\
![mapping export configuration](https://user-images.githubusercontent.com/48808497/202928899-1d90bdfe-d8bd-4565-8e94-23e7fce2e8b8.png)

