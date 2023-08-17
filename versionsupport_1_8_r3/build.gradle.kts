plugins {
    id("com.tomkeuper.bedwars.java-conventions")
}

dependencies {
    implementation(project(":bedwars-api"))
    implementation(project(":versionsupport_common"))
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}

description = "versionsupport_1_8_r3"
