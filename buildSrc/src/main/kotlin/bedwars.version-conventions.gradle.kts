plugins {
    id("bedwars.standard-conventions")
    id("com.gradleup.shadow")
}

tasks {
    shadowJar {
        archiveFileName.set("BedWars-${project.version}.jar")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
