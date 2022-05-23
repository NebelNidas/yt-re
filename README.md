# YT-Mappings
YT-Mappings is a set of open, unencumbered YouTube app mappings, free for everyone to use under the Creative Commons Zero license.
The intention is to let everyone mod YouTube freely and openly, while also being able to innovate and process the mappings as they see fit.

To see the current version being targeted, check the branch name!


## Usage
To get started,
To obtain a deobfuscated YouTube JAR, [`./gradlew mapNamedJar`](#mapNamedJar) will generate a jar named like `youtube-<youtube version>-named.jar`, which can be sent to a decompiler for deobfuscated code.

Please note, to run our build script **Java 16** or higher is required!


## Contributing
Please remember that copying and pasting mappings from alternate projects under more restrictive licenses is **completely forbidden** without explicit permission from the owners of said mappings to distribute the names under the CC0 license.
This includes using the names from those mappings for inspiration.
Discussing the naming approaches used in said projects is also not welcome - you have been warned.
However, it is a good idea to consult name changes with other people - use pull requests or our community spaces to ask questions!

Please have a look at the [naming conventions](/CONVENTIONS.md) before submitting mappings.

### Getting Started
1. Fork and clone the repo
2. Download the correct version of the YouTube APK (version number see [build.gradle](build.gradle)), preferably from [APKMirror](https://www.apkmirror.com/apk/google-inc/youtube/)
3. Place the APK at the root of this directory and rename it to `youtube.apk`
4. Run `./gradlew startMapping` to do some magic behind the scenes and open [Enigma](https://github.com/FabricMC/Enigma), a user interface to easily edit the mappings
5. Commit and push your work to your fork
6. Open a pull request with your changes


## Gradle
YT-Mappings uses Gradle to provide a number of utility tasks for working with the mappings.

### `startMapping`
Setup and download and launch the latest version of [Enigma](https://github.com/FabricMC/Enigma) automatically configured to use the merged jar and the mappings.

Compared to launching Enigma externally, the gradle task adds a name guesser plugin that automatically maps enums and a few constant field names.

### `build`
Build a GZip'd archive containing a tiny mapping between official (obfuscated), [intermediary](https://github.com/NebelNidas/intermediary), and YT-Mappings names ("named") and packages enigma mappings into a zip archive.

### `mapNamedJar`
Builds a deobfuscated jar with YT-Mappings and automapped fields (enums, etc.). Unmapped names will be filled with [intermediary](https://github.com/NebelNidas/Intermediary) names.

### `decompileCFR`
Decompile the mapped source code. **Note:** This is not designed to be recompiled.
