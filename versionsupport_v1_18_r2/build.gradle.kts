dependencies {
    compileOnly(projects.bedwarsApi)
    implementation(projects.versionsupportCommon)
    compileOnly("org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.release.set(17)
}

description = "versionsupport_v1_18_r2"