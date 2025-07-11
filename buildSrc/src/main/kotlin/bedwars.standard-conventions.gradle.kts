plugins {
    id("bedwars.base-conventions")
    id("com.gradleup.shadow")
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}
