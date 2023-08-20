plugins {
    id("bedwars.base-conventions")
    id("com.github.johnrengelman.shadow")
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "tomkeuper"
            url = uri("https://repo.tomkeuper.com/releases")
            credentials(PasswordCredentials::class)
        }
    }
    publications.create<MavenPublication>("mavenJava") {
        groupId = rootProject.group as String
        artifactId = "bedwars-${project.name}"
        version = rootProject.version as String
        from(components["java"])
    }
}