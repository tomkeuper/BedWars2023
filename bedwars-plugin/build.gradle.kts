plugins {
    id("com.tomkeuper.bedwars.java-conventions")
}

dependencies {
    api("io.papermc:paperlib:1.0.8")
    api("mysql:mysql-connector-java:8.0.29")
    api("org.bstats:bstats-bukkit:3.0.2")

    api(project(":bedwars-api"))
    api(project(":versionsupport_1_8_r3"))
    api(project(":resetadapter-slime"))
    api(project(":resetadapter_aswm"))
    api(project(":resetadapter_slimepaper"))
    api(project(":versionsupport_1_12_r1"))
    api(project(":versionsupport_v1_16_r3"))
    api(project(":versionsupport_v1_17_r1"))
    api(project(":versionsupport_v1_18_r2"))
    api(project(":versionsupport_v1_19_r3"))
    api(project(":versionsupport_v1_20_r1"))
    api(project(":versionsupport-common"))

    api("com.andrei1058.vipfeatures:vipfeatures-api:[1.0,)")
    api("com.zaxxer:HikariCP:5.0.1")
    api("org.slf4j:slf4j-simple:2.0.6")
    api("com.h2database:h2:2.2.220")

    compileOnly("de.simonsator:Party-and-Friends-MySQL-Edition-Spigot-API:1.5.4-RELEASE")
    compileOnly("de.simonsator:Spigot-Party-API-For-RedisBungee:1.0.3-SNAPSHOT")
    compileOnly("de.simonsator:DevelopmentPAFSpigot:1.0.67")
    compileOnly("com.alessiodp.parties:parties-api:3.2.9")
    compileOnly("net.citizensnpcs:citizens-main:2.0.30-SNAPSHOT")
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("me.neznamy:tab-api:4.0.2")
    compileOnly("de.dytanic.cloudnet:cloudnet-wrapper-jvm:3.4.5-RELEASE")
}

description = "bedwars-plugin"
