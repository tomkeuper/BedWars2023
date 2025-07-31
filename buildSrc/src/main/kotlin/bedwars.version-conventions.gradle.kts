plugins {
    id("bedwars.standard-conventions")
    id("com.gradleup.shadow")
}

tasks {
    shadowJar {
        archiveFileName = "BedWars-${project.version}.jar"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
