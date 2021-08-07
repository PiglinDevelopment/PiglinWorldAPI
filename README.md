# PiglinWorldAPI
A set of APIs and utilities that PiglinWorld's plugins use.

![Build](https://github.com/PiglinDevelopment/PiglinWorldAPI/actions/workflows/push.yml/badge.svg?branch=master)
![Maven central version](https://img.shields.io/maven-central/v/dev.piglin/piglinworldapi)

It makes it possible to create your own blocks, provides a higher-level Inventory APIs to create GUIs, crafting recipes, wraps over commonly used APIs of other plugins and miscellaneous utils.

## Add to your dependencies
1. Add `PiglinWorldAPI` to depend or softdepend of your plugin.yml
2. Add a dependency to your build system
#### Maven:
```xml
<dependency>
    <groupId>dev.piglin</groupId>
    <artifactId>piglinworldapi</artifactId>
    <version>{VERSION}</version>
    <scope>provided</scope>
</dependency>
```
If you want to use snapshots (versions ending with -SNAPSHOT which are updated on every commit),
run maven with `-U` (--update-snapshots) and add a snapshot repository:
```xml
<repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```
And be ready to see your code doesn't compile tomorrow because PiglinWorldAPI will have breaking changes (at least until it's 1.0.0)
#### Gradle:
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'mc.piglin:piglinworldapi:{VERSION}'
}
```

## Build from source
```sh
git clone https://github.com/PiglinDevelopment/PiglinWorldAPI
cd PiglinWorldAPI
mvn package
```
