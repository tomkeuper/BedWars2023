dependencies {
    compileOnly(projects.bedwarsApi)
    api("com.flowpowered:flow-nbt:2.0.2")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.grinderwolf:slimeworldmanager-api:2.8.0-SNAPSHOT")
    compileOnly("commons-io:commons-io:2.11.0")
}

tasks.compileJava {
    options.release.set(11)
}
