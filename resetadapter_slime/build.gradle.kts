plugins {
    `java-library`
}

dependencies {
    compileOnly(projects.bedwarsApi)
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.grinderwolf:slimeworldmanager-api:2.2.1")
    compileOnly("commons-io:commons-io:2.11.0")
}

tasks.compileJava {
    options.release.set(11)
}
