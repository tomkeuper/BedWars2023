plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

dependencies {
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("io.freefair.gradle:lombok-plugin:8.0.1")
}