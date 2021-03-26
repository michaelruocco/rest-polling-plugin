# Library Template

[![Build](https://github.com/michaelruocco/rest-polling-plugin/workflows/pipeline/badge.svg)](https://github.com/michaelruocco/rest-polling-plugin/actions)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/github/michaelruocco/rest-polling/com.github.michaelruocco.rest-polling.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=gradle%20plugin)](https://plugins.gradle.org/plugin/com.github.michaelruocco.rest-polling)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Usage

You can apply the plugin to your build by adding the following:

```gradle
plugins {
    id "com.github.michaelruocco.rest-polling" version "0.1.0"
}
```

Configuring a task to poll a rest endpoint:

```
pollRestGetEndpoint {
    uri = "http//localhost:8080/actuator/health"
    minConsecutiveValidRequests = 1 //optional defaults to 1
    expectedStatus = 200 //optional, defaults to 200
    expectedValues = [ "build.version": "0.1.0" ]
    delay = "PT0S" //optional, defaults to 0
    interval = "PT1S" //optional, defaults to 1 second
    atMost = "PT10S" //optional, defaults to 10 seconds
}
```

## Useful Commands

```gradle
// cleans build directories
// prints currentVersion
// formats code
// builds code
// runs tests
// checks for gradle issues
// checks dependency versions
./gradlew clean currentVersion dependencyUpdates lintGradle spotlessApply build
```