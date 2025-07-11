plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.8")
    implementation("io.freefair.gradle:lombok-plugin:8.6")
}