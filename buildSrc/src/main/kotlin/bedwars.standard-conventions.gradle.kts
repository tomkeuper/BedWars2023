plugins {
    id("bedwars.base-conventions")
    id("io.github.goooler.shadow")
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}
