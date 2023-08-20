dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api("com.iridium:IridiumColorAPI:1.0.6")
    implementation("commons-lang:commons-lang:2.6") // Used by UridiumColorAPI
    compileOnly("com.google.common:google-collect:1.0")
    compileOnly("me.neznamy:tab-api:3.2.4")
}

tasks.javadoc {
    enabled = true
}
