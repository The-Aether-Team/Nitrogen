<p align="center"><img width="250" height="250" src="assets/logo.webp"></p>

# Nitrogen
[![Modloader: Forge](https://img.shields.io/badge/mod%20loader-forge-CC974D?style=flat-square)](https://files.minecraftforge.net/net/minecraftforge/forge/)
[![Modloader: NeoForge](https://img.shields.io/badge/mod%20loader-neoforge-E08234?style=flat-square)](https://neoforged.net/)
[![Modloader: Fabric](https://img.shields.io/badge/mod%20loader-fabric-1F6FEB?style=flat-square)](https://fabricmc.net/use/installer/)
[![CircleCI](https://circleci.com/gh/The-Aether-Team/Nitrogen/tree/1.20.1-develop.svg?style=shield)](https://app.circleci.com/pipelines/github/The-Aether-Team/Nitrogen?branch=1.20.1-develop)
[![Code license (LGPL v3.0)](https://img.shields.io/badge/code%20license-LGPL%20v3.0-green.svg?style=flat-square)](https://github.com/The-Aether-Team/Nitrogen/blob/1.20.1-develop/LICENSE.txt)

Nitrogen is a library mod used by The Aether Team to abstract code that is usable by both The Aether and The Aether II to allow for easier maintenance and organization. This library is intended for usage by The Aether Team, and isn't particularly useful to use or possible to contribute to if you're an outside developer. This library will also not be released on CurseForge. This repository is only public for visibility and ease of use for the team.

## :package: Download the latest releases
### Packages
To install this mod through GitHub Packages in Gradle for development, you can use a redirect for the specific project you desire:

<details>
<summary> Repositories Code</summary>

```
repositories {
  ...
  maven { url = "https://packages.aether-mod.net/Nitrogen" }
}
```

</details>

Then load it through your dependencies, with `project.nitrogen_version` specified in the `gradle.properties`:

<details>
<summary> Dependencies Code</summary>

```
dependencies {
  ...
  implementation fg.deobf("com.aetherteam.nitrogen:nitrogen_internals:${project.nitrogen_version}")
  ...
  jarJar fg.deobf("com.aetherteam.nitrogen:nitrogen_internals:${project.nitrogen_version}") {
    jarJar.ranged(it, "[${project.nitrogen_version},)")
    jarJar.pin(it, "${project.nitrogen_version}")
  }
}
```

</details>
