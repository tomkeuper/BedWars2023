dependencies {
    compileOnly(projects.bedwarsApi)
    implementation(projects.versionsupportCommon)
    compileOnly("org.spigotmc:spigot:1.20.3-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.release.set(17)
}

repositories {
    // Important Repos
    mavenCentral()
    mavenLocal()
    maven("https://repo.codemc.io/repository/nms/") // Spigot
    maven("https://papermc.io/repo/repository/maven-public/") // com.mojang (dep of Spigot)
}

description = "versionsupport_v1_20_r3"
