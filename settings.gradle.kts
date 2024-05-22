rootProject.name = "BedWars2023" // this thing was "bedwars2023" and changed it to "BedWars2023"

// core
include(":bedwars-api")
include(":bedwars-plugin")

// world managers
include(":resetadapter_aswm")
include(":resetadapter_slimepaper")
include(":resetadapter_slime")

include(":versionsupport_1_8_r3")
include(":versionsupport_1_12_r1")
include(":versionsupport_v1_16_r3")
include(":versionsupport_v1_17_r1")
include(":versionsupport_v1_18_r2")
include(":versionsupport_v1_19_r3")
include(":versionsupport_v1_20_r1")
include(":versionsupport_v1_20_r2")
include(":versionsupport_v1_20_r3")
include(":versionsupport_v1_20_r5")
include(":versionsupport_common")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://repo.tomkeuper.com/repository/releases/") // SlimJar
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("io.alcide.gradle-semantic-build-versioning") version "4.2.2" // Used for automatic versioning
}