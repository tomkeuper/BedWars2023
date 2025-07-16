plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:9.0.0-beta6")
    implementation("io.freefair.gradle:lombok-plugin:8.6")
}