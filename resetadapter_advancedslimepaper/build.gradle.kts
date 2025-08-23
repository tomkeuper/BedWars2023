dependencies {
    compileOnly(projects.bedwarsApi)
    api("com.flowpowered:flow-nbt:2.0.2")
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT"){
        exclude("commons-lang", "commons-lang")
    }
    compileOnly("com.infernalsuite.asp:api:4.0.0")
    implementation("com.infernalsuite.asp:file-loader:4.0.0")
    compileOnly("org.jetbrains:annotations:26.0.2")
    compileOnly("commons-io:commons-io:2.11.0")
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.papermc.io/repository/maven-public/") // bungeecord-chat (dep of spigot-api)
    maven("https://repo.rapture.pw/repository/maven-releases/") // Flow-NBT
    maven("https://repo.infernalsuite.com/repository/maven-releases/") // ASP:api
}