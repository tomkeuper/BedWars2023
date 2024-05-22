
plugins {
    `java-library`
    id("io.github.goooler.shadow")
    id("io.freefair.lombok")
}

// Global dependencies
dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
}

tasks {
    javadoc {
        // This saves a decent bit of processing power on slow machines
        enabled = false
        options.encoding = Charsets.UTF_8.name()
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(11)
        options.compilerArgs.addAll(listOf("-nowarn", "-Xlint:-unchecked", "-Xlint:-deprecation"))
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
