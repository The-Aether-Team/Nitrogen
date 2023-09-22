# Nitrogen
[![Modloader: Forge](https://img.shields.io/badge/mod%20loader-forge-CC974D?style=flat-square)](https://files.minecraftforge.net/net/minecraftforge/forge/)
[![CircleCI](https://circleci.com/gh/The-Aether-Team/Nitrogen/tree/1.19.4-develop.svg?style=shield)](https://app.circleci.com/pipelines/github/The-Aether-Team/Nitrogen?branch=1.19.4-develop)
[![Code license (LGPL v3.0)](https://img.shields.io/badge/code%20license-LGPL%20v3.0-green.svg?style=flat-square)](https://github.com/The-Aether-Team/Nitrogen/blob/1.19.4-develop/LICENSE.txt)

Nitrogen is a library mod used by The Aether Team to abstract code that is usable by both The Aether and The Aether II to allow for easier maintenance and organization. This library is intended for usage by The Aether Team, and isn't particularly useful to use or possible to contribute to if you're an outside developer. This library will also not be released on CurseForge. This repository is only public for visibility and ease of use for the team.

## :package: Download the latest releases
### Packages
To install this mod through GitHub Packages in Gradle for development, you will need the [Gradle Github Packages Plugin](https://github.com/0ffz/gpr-for-gradle). To use it, make sure you have access to the Gradle plugins maven and the plugin as a buildscript dependency:

<details>
<summary> Buildscript Code</summary>

`settings.gradle`
```
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

`build.gradle`
```
plugins {
    id 'io.github.0ffz.github-packages' version '[1,2)'
}
```

</details>

Then you need to specify the package you want to use in your repository:

<details>
<summary> Repositories Code</summary>

```
repositories {
  ...
  maven githubPackage.invoke("The-Aether-Team/Nitrogen")
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
