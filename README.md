taam [![Build Status](https://travis-ci.org/Team-IO/taam.svg)](https://travis-ci.org/Team-IO/taam)
====
Tech and Accessory Mod

For details on how to use this mod, check the [Wiki](https://github.com/Team-IO/taam/wiki).

Also find this mod on [CurseForge](http://minecraft.curseforge.com/projects/taam) and [on our homepage](https://team-io.net/taam.php).

## Contributing
If you want to contribute, you can do so [by reporting bugs](https://github.com/Team-IO/taam/wiki), [by helping fix the bugs](https://github.com/Team-IO/taam/pulls) or by spreading the word!

You are also welcome to [support us on Patreon](https://www.patreon.com/Team_IO?ty=h)!

## Building the mod
Taam uses a fairly simple implementation of ForgeGradle. To build a ready-to-use jar, you can use the gradle wrapper delivered with the rest of the source code.  
For Windows systems, run this in the console:
    gradlew.bat build
For *nix systems, run this in the terminal:
    ./gradlew build
Installed Gradle versions should also work fine.

## Some info on the internal structure:
Mod & Dependency versions are controlled in the upper half of the build.gradle. All mod metadata is done in code, with the version replaced by gradle on compile time.  
There is a dependencies.info file that denots the dependency libraries to be downloaded / checked on launch. The versions in there are also replaced by Gradle. - The responsible class is DepLoader, largely 'borrowed' from ChickenBones. (Thanks, CB! That class works like a charm!)

Item/Block names are recorded in the class net.teamio.taam.Taam. Main mod class is net.teamio.taam.TaamMain. Anything else in that package is somewhat related to global registration with Minecraft or config stuff.

All Item & block classes (and related stuff) are located in the net.teamio.taam.content.* packages, clustered by area.
Supporting classes for the themes (Utils, API, etc.) are located in the corresponding net.teamio.taam.* packages.

Integrations with other mods belong in a corresponding package in net.teamio.taam.integration, gui, rendering and network in their respective package in net.teamio.taam.