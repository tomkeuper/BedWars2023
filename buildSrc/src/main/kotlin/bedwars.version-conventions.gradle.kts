plugins {
    id("bedwars.standard-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        archiveFileName.set("BedWars-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        relocate("com.flowpowered.nbt", "com.tomkeuper.bedwars.libs.flow")
    }
}
