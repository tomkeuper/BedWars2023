dependencies {
    compileOnly(projects.bedwarsApi)
    compileOnly("org.spigotmc:spigot:1.14.4-R0.1-SNAPSHOT")
}

repositories {
    // Important Repos
    mavenCentral()
//    mavenLocal()
    maven("https://repo.codemc.io/repository/nms/") // Spigot
}

description = "versionsupport_common"