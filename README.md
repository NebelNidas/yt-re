# YouTube App Reverse Engineering Suite
YT-RE provides several facilities aiding the YouTube Android app reverse engineering process. These include our own set of deobfuscation mappings, tracking of obfuscated artifacts across releases, integration with several well-known reverse engineering tools and a local Git repository generator.

## Getting Started
1. Clone the repo.
2. Download the correct version (see current branch name) of the YouTube APK, preferably from [APKMirror](https://www.apkmirror.com/apk/google-inc/youtube/) (`nodpi` is the default).
3. Place the APK at the root of this directory and rename it to `youtube-<version>.apk` (replace `<version>` with the adequate version string).
4. Run `./gradlew enigma` or `/gradlew jadx` to use and/or edit the mappings (see the tasks' descriptions below).
5. If you want to, commit and push your work to a fork and open a PR with your changes. Please have a look at the [naming conventions](/CONVENTIONS.md) before submitting one.

## Gradle Tasks
YT-RE uses Gradle to provide a number of utility tasks for working with the mappings.
Please note, to run our build script **Java 17** is required!

### `enigma`
Download, setup and launch the latest version of [Enigma](https://github.com/FabricMC/Enigma) automatically configured to use the converted jar and mappings.

Compared to launching Enigma externally, the gradle task adds a name guesser plugin that automatically maps enums and a few constant field names.

### `jadx`
Download, setup and launch the latest version of [JADX](https://github.com/skylot/jadx) automatically configured to use the provided APK and mappings.

Please do not enable JADX's built-in auto-deobfuscation, as this feature can mess with exported package names.

### `buildTinyMappingFiles`
Build Tiny and Tiny v2 mapping files between official (obfuscated) names and our renames ("named").

### `buildNamedJar`
Builds a deobfuscated jar with deobfuscation mappings applied (`youtube-<yt-version>-named.jar`).

### Tasks for updating to a new YT version
These are of no use to the average user, but are listed here anyway for completeness' sake. To update the project to a new YT version, repeat steps 2 and 3 from [Getting Started](#getting-started), but this time with the YT version you want to update to. Then run the following tasks. Note that they all require passing an additional `newYtVersion` property via `-PnewYtVersion='<new-version>'`.

#### `matcher`
Run [Matcher](https://github.com/NebelNidas/Matcher) with the provided `youtube-<current-version>.apk` and `youtube-<new-version>.apk` as inputs, so you can update the mappings to a newer YT version.

As soon as Matcher is started and has analyzed its input files, select `Matching` → `Auto match all`. Depending on your Computer's hardware, this may take several hours. A modern multi-core CPU with at least 6 cores and 16 GB of RAM are recommended! Matcher itself requires at least 6 GB of memory. After the process has finished, take care of the remaining red entries. You may have to double-check each class and method if any containing children are still unmatched. If you can't find a similar enough counterpart, perhaps the class/method/etc. has been removed from the newer APK. In this case, click the "unmatchable" button in the bottom pane.

Once you're confident with your results, go to `File` → `Save matches` and save the file into the `matches` folder. Please name it `<current-version> - <new-version>.match`; see the already existent match files.

To update the mappings, go to to `File` → `Save mappings (Enigma)`, select the `mappings` folder, give permission to overwrite the existing data and use the following configuration to export the updated files:\
![mapping export configuration](https://user-images.githubusercontent.com/48808497/202928899-1d90bdfe-d8bd-4565-8e94-23e7fce2e8b8.png)

#### `jadxNewYtVersion`
Opens the new YouTube version's APK in JADX. This task should only be used for assisting in the matching process, don't import or export any mappings from there.

#### `updateIntermediary`
Generates a new Tiny file with updated intermediaries into the [`./intermediary`](./intermediary/) directory.
