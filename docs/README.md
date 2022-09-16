# Nitrogen
[![Mod loader: Forge](https://img.shields.io/badge/mod%20loader-forge-CC974D?style=flat-square)](https://files.minecraftforge.net/net/minecraftforge/forge/)
[![Gilded-Games](https://circleci.com/gh/Gilded-Games/Nitrogen/tree/1.19.svg?style=shield)](https://app.circleci.com/pipelines/github/Gilded-Games/Nitrogen?branch=1.19)
[![Code license (LGPL v3.0)](https://img.shields.io/badge/code%20license-LGPL%20v3.0-green.svg?style=flat-square)](https://github.com/Gilded-Games/Nitrogen/blob/1.19/LICENSE.txt)

Nitrogen is a library mod used by Gilded Games to abstract code that is usable by both Aether I and Aether II to allow for easier maintenance and organization. This library is intended for usage by Gilded Games, and isn't particularly useful to use or possible to contribute to if you're an outside developer. This library will also not be released on CurseForge. This repository is only public for visibility and ease of use for the team.

## :package: Download the latest releases
### Packages
To install this mod through GitHub Packages in Gradle for development, you will need the [Gradle Github Packages Plugin](https://github.com/0ffz/gpr-for-gradle). To use it, make sure you have access to the Gradle plugins maven and the plugin as a buildscript dependency:
```
buildscript {
  repositories {
    ...
    maven {
        name 'Gradle'
        url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    ...
    classpath group: 'io.github.0ffz', name: 'gpr-for-gradle', version: '1.+', changing: true
  }
}
```
Then you need to specify the package you want to use in your repository:
```
repositories {
  ...
  maven githubPackage.invoke("Gilded-Games/Nitrogen")
}
```
Then load it through your dependencies, with `project.nitrogen_version` specified in the `gradle.properties`:
```
dependencies {
  ...
  compileOnly "com.gildedgames.nitrogen:nitrogen:${project.nitrogen_version}"
  runtimeOnly fg.deobf("com.gildedgames.nitrogen:nitrogen:${project.nitrogen_version}")
  ...
  jarJar fg.deobf("com.gildedgames.nitrogen:nitrogen:${project.nitrogen_version}") {
    jarJar.ranged(it, "[${project.nitrogen_version},)")
    jarJar.pin(it, "${project.nitrogen_version}")
  }
}
```
