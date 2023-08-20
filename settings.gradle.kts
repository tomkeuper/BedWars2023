rootProject.name = "bedwars2023"

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
include(":versionsupport_common")


pluginManagement {
    repositories {
        maven("https://repo.spongepowered.org/repository/maven-public/")
        gradlePluginPortal()
        mavenCentral()
    }
}


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral() // Netty, SnakeYaml, json-simple, slf4j, Guava, Kyori event, bStats, AuthLib
        mavenLocal()
            // Important Repos
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.tomkeuper.com/repository/bedwars-releases/")
    maven("https://papermc.io/repo/repository/maven-public/")

    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.andrei1058.dev/releases/")

    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")

    maven("https://repo.maven.apache.org/maven2/")
    maven("https://simonsator.de/repo/")
    maven("https://maven.citizensnpcs.co/repo")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://repo.cloudnetservice.eu/repository/releases/")
    maven("https://repo.fusesource.com/nexus/content/repositories/releases-3rd-party/")
    maven("https://repo.kryptonmc.org/releases")
    maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
    maven("https://repo.glaremasters.me/repository/concuncan/")
    maven("https://repo.rapture.pw/repository/maven-snapshots/")
    maven("https://repo.rapture.pw/repository/maven-releases/")
    maven("https://repo.infernalsuite.com/repository/maven-snapshots/")


    }
}