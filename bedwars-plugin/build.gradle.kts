import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

dependencies {
    api("io.papermc:paperlib:1.0.8")
    api("org.bstats:bstats-bukkit:3.0.2")

    api(projects.bedwarsApi)
    api(projects.versionsupportCommon)
    api(projects.versionsupport18R3)
    api(projects.versionsupport112R1)
    api(projects.versionsupportV116R3)
    api(projects.versionsupportV117R1)
    api(projects.versionsupportV118R2)
    api(projects.versionsupportV119R3)
    api(projects.versionsupportV120R1)

    api("com.andrei1058.vipfeatures:vipfeatures-api:[1.0,)")
    api("com.zaxxer:HikariCP:5.0.1") {
        exclude("slf4-j-api", "slf4-j-api")
    }
    compileOnly("org.slf4j:slf4j-simple:2.0.6")
    api("com.h2database:h2:2.2.220")
    api("commons-io:commons-io:2.11.0") // for resetadapters
    api("mysql:mysql-connector-java:8.0.29"){
        exclude("com.google.protobuf", "protobuf-java")
    }

    compileOnly("de.simonsator:Party-and-Friends-MySQL-Edition-Spigot-API:1.5.4-RELEASE")
    compileOnly("de.simonsator:Spigot-Party-API-For-RedisBungee:1.0.3-SNAPSHOT")
    compileOnly("de.simonsator:DevelopmentPAFSpigot:1.0.67")
    compileOnly("com.alessiodp.parties:parties-api:3.2.9")
    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("me.neznamy:tab-api:4.0.2")
    compileOnly("de.dytanic.cloudnet:cloudnet-wrapper-jvm:3.4.5-RELEASE")
}


bukkit {
    name = "BedWars2023"
    main = "com.tomkeuper.bedwars.BedWars"
    apiVersion = "1.13"

    author = "Mr. Ceasar"
    description = "BedWars minigame by Tom Keuper forked from BedWars1058"
    version = "${project.version}"

    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    depend = listOf("TAB")
    softDepend = listOf(
            "Vault",
            "PlaceholderAPI",
            "Citizens",
            "Parties",
            "SlimeWorldManager",
            "VipFeatures",
            "Enhanced-SlimeWorldManager",
            "PartyAndFriends",
            "Spigot-Party-API-PAF",
    )
}

tasks.compileJava {
    options.release.set(11)
}
val versions = setOf(
    projects.versionsupportCommon,
    projects.versionsupport18R3,
    projects.versionsupport112R1,
    projects.versionsupportV116R3,
    projects.versionsupportV117R1,
    projects.versionsupportV118R2,
    projects.versionsupportV119R3,
    projects.versionsupportV120R1,
    projects.resetadapterSlime,
    projects.resetadapterSlimepaper,
    projects.resetadapterAswm
).map { it.dependencyProject }

tasks {
    shadowJar {
        archiveFileName.set("BedWars-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        fun registerPlatform(project: Project, shadeTask: org.gradle.jvm.tasks.Jar) {
            dependsOn(shadeTask)
            dependsOn(project.tasks.withType<Jar>())
            from(zipTree(shadeTask.archiveFile))
        }

        versions.forEach {
            registerPlatform(it, it.tasks.named<ShadowJar>("shadowJar").get())
        }

        relocate("com.iridium.iridiumcolorapi", "com.tomkeuper.bedwars.libs.color")
        relocate("io.papermc.lib", "com.tomkeuper.bedwars.libs.paper")
        relocate("org.slf4j", "com.tomkeuper.bedwars.libs.slf4j")
        relocate("org.h2", "com.tomkeuper.bedwars.libs.h2")
        relocate("org.bstats", "com.tomkeuper.bedwars.libs.bstats")
        relocate("com.zaxxer.hikari", "com.tomkeuper.bedwars.libs.hikari")
        relocate("com.andrei1058.vipfeatures.api", "com.tomkeuper.bedwars.libs.vipfeatures")
        relocate("com.mysql", "com.tomkeuper.bedwars.libs.mysql")
        relocate("org.apache", "com.tomkeuper.bedwars.libs.apache")

    }
    build {
        dependsOn(shadowJar)
    }
}
