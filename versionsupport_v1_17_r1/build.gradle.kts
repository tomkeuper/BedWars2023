dependencies {
    compileOnly(projects.bedwarsApi)
    implementation(project(":versionsupport_common"))
    compileOnly("org.spigotmc:spigot:1.17.1-R0.1-SNAPSHOT")
}


tasks.compileJava {
    options.release.set(17)
}

description = "versionsupport_v1_17_r1"
