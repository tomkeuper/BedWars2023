dependencies {
    compileOnly(projects.bedwarsApi)
    implementation(project(":versionsupport_common"))
    compileOnly("org.spigotmc:spigot:1.19.4-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.release.set(17)
}

description = "versionsupport_v1_19_r3"
