allprojects {
    group = "com.tomkeuper.bedwars"
    version = "1.0-SNAPSHOT"
    description = "BedWars minigame by Tom Keuper forked from BedWars1058"

    ext.set("id", "bedwars")
    ext.set("website", "https://github.com/tomkeuper/BedWars2023")
    ext.set("author", "Mr. Ceasar")
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
    projects.versionsupportV120R2,
    projects.versionsupportV120R3,
    projects.versionsupportV120R4,
    projects.resetadapterSlime,
    projects.resetadapterSlimepaper,
    projects.bedwarsApi,
    projects.resetadapterAswm
).map { it.dependencyProject }

val special = setOf(
    projects.bedwarsPlugin,
).map { it.dependencyProject }

subprojects {
    when (this) {
        in versions -> plugins.apply("bedwars.version-conventions")
        in special -> plugins.apply("bedwars.standard-conventions")
        else -> plugins.apply("bedwars.base-conventions")
    }
}
