dependencies {
    compileOnly(projects.bedwarsApi)
    implementation(project(":versionsupport_common"))
    compileOnly("org.spigotmc:spigot:1.12.2-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.release.set(11)
}

repositories {
    // Important Repos
    mavenCentral()
    mavenLocal()
    maven("https://repo.codemc.io/repository/nms/") // Spigot
}

description = "versionsupport_1_12_r1"
