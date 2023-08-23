plugins {
    id("bedwars.base-conventions")
    id("com.github.johnrengelman.shadow")
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}
