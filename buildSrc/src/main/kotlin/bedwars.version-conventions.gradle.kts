plugins {
    id("bedwars.standard-conventions")
    id("io.github.goooler.shadow")
}

tasks {
    shadowJar {
        archiveFileName.set("BedWars-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
