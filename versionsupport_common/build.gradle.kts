plugins {
    id("com.tomkeuper.bedwars.java-conventions")
}

dependencies {
    implementation(project(":bedwars-api"))
    compileOnly("org.spigotmc:spigot:1.14.4-R0.1-SNAPSHOT")
}

description = "versionsupport_common"
