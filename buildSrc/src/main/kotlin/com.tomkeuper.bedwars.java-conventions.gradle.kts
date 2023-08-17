plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()

    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://repo.codemc.io/repository/nms/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://simonsator.de/repo/") }
    maven { url = uri("https://maven.citizensnpcs.co/repo") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.alessiodp.com/releases/") }
    maven { url = uri("https://repo.andrei1058.dev/releases/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.tomkeuper.com/repository/bedwars-releases/") }
    maven { url = uri("https://repo.cloudnetservice.eu/repository/releases/") }
    maven { url = uri("https://repo.fusesource.com/nexus/content/repositories/releases-3rd-party/") }
    maven { url = uri("https://repo.kryptonmc.org/releases") }
    maven { url = uri("https://nexus.iridiumdevelopment.net/repository/maven-releases/") }
    maven { url = uri("https://repo.glaremasters.me/repository/concuncan/") }
    maven { url = uri("https://repo.rapture.pw/repository/maven-snapshots/") }
    maven { url = uri("https://repo.rapture.pw/repository/maven-releases/") }
    maven { url = uri("https://repo.infernalsuite.com/repository/maven-snapshots/") }
}

dependencies {
    compileOnly("net.md-5:bungeecord-chat:1.8-SNAPSHOT")
    compileOnly("commons-io:commons-io:2.11.0")
    compileOnly("org.jetbrains:annotations:24.0.1")
}

group = "com.tomkeuper.bedwars"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
