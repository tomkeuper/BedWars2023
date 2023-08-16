plugins {
    id("com.tomkeuper.bedwars.java-conventions")
}

dependencies {
    api("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
    api("com.iridium:IridiumColorAPI:1.0.6")

    compileOnly("com.google.common:google-collect:1.0")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.neznamy:tab-api:3.2.4")
}

description = "bedwars-api"
