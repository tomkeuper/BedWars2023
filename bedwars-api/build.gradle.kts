plugins {
    java
    `maven-publish`
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT"){
        exclude("commons-lang", "commons-lang")
    }
    compileOnly("com.iridium:IridiumColorAPI:1.0.9")
    compileOnly("org.apache.commons:commons-lang3:3.14.0") // Used by UridiumColorAPI
    compileOnly("me.neznamy:tab-api:3.2.4")
}

tasks.javadoc {
    enabled = true
}

repositories{
    mavenCentral()
    mavenLocal()
    maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/") // IridiumColorAPI
    maven("https://repo.kryptonmc.org/releases") // TAB
    maven("https://repo.codemc.io/repository/nms/") // Spigot
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group as String
            version = rootProject.version as String
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = if (project.version.toString().endsWith("SNAPSHOT")) {
                uri("https://repo.tomkeuper.com/repository/snapshots/")
            } else {
                uri("https://repo.tomkeuper.com/repository/releases/")
            }

            credentials {
                username = project.findProperty("deployUsername").toString()
                password = project.findProperty("deployPassword").toString()
            }
        }
    }
}