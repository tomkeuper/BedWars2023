dependencies {
    compileOnly(projects.bedwarsApi)
    implementation(projects.versionsupportCommon)
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
}

tasks.compileJava {
    options.release.set(17)
}

repositories {
    // Important Repos
    mavenCentral()
    mavenLocal()
    maven("https://repo.codemc.io/repository/nms/") // Spigot
}

description = "versionsupport_v1_17_r1"
