allprojects {
    group = "com.tomkeuper.bedwars"
    version = rootProject.version
    description = "BedWars minigame by Tom Keuper forked from BedWars1058"

    ext.set("id", "bedwars")
    ext.set("website", "https://github.com/tomkeuper/BedWars2023")
    ext.set("author", "Mr. Ceasar")
}

val versions = setOf(
    ":versionsupport_common",
    ":versionsupport_1_8_r3",
    ":versionsupport_1_12_r1",
    ":versionsupport_v1_16_r3",
    ":versionsupport_v1_17_r1",
    ":versionsupport_v1_18_r2",
    ":versionsupport_v1_19_r3",
    ":versionsupport_v1_20_r4",
    ":versionsupport_v1_20_6",
    ":versionsupport_v1_21_r1",
    ":versionsupport_v1_21_r2",
    ":versionsupport_v1_21_r3",
    ":resetadapter_slime",
    ":resetadapter_slimepaper",
    ":resetadapter_advancedslimepaper",
    ":resetadapter_aswm",
    ":bedwars-api"
)

val special = setOf(
    ":bedwars-plugin"
)

subprojects {
//    println("Project: ${path}")
    when (path) {
        in versions -> plugins.apply("bedwars.version-conventions")
        in special -> plugins.apply("bedwars.standard-conventions")
        else -> plugins.apply("bedwars.base-conventions")
    }
}

tasks.register("printTag") {
    doLast {
        println("Generated Tag: ${rootProject.version}")
    }
}
